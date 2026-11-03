package com.switsh.app.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.util.Patterns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.switsh.app.R
import com.switsh.app.model.Block
import com.switsh.app.model.ScreenLabels
import com.switsh.app.domains.onboarding.ContactType
import com.switsh.app.domains.onboarding.CustomerType
import com.switsh.app.domains.onboarding.IncomeBand
import com.switsh.app.domains.onboarding.PersonalProfileRequest
import com.switsh.app.domains.onboarding.ProfileSelectionRequest
import com.switsh.app.domains.onboarding.RegisterRequest
import com.switsh.app.domains.onboarding.Sex
import com.switsh.app.domains.onboarding.SourceOfFunds
import com.switsh.app.domains.onboarding.TermsRequest
import com.switsh.app.domains.onboarding.DocumentType
import com.switsh.app.domains.onboarding.IdentityDocumentRequest
import com.switsh.app.domains.onboarding.UploadSessionRequest
import com.switsh.app.domains.onboarding.VerifyContactRequest
import com.switsh.app.ui.ScreenState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/** "Onboarding" section: registration, KYC/KYB steps and verification status. */
class OnboardingFragment : ScreenFragment() {
    private class GeoAddress(val address: String, val latitude: Double, val longitude: Double)

    private var geoAddress: GeoAddress? = null
    private var pendingGeolocation: ((Boolean) -> Unit)? = null
    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        val accept = pendingGeolocation ?: return@registerForActivityResult
        pendingGeolocation = null
        if (grants.values.any { it }) locate(accept) else geolocationFailed(accept)
    }

    override fun primary(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        if (screenId == "onb-infos-perso") {
            navigator.appState.saveProfileName(
                state.inputValues["givenNames"].orEmpty(),
                state.inputValues["surname"].orEmpty(),
            )
            savePersonalProfile(button, state, onComplete)
            return
        }
        if (screenId == "onb-inscription") {
            register(button, state, onComplete)
            return
        }
        if (screenId == "onb-id-particulier") {
            attachIdentityDocument(button, state, onComplete)
            return
        }
        if (screenId == "onb-justif-domicile") {
            submitApplication(button, onComplete)
            return
        }
        if (screenId == "onb-auth-security") {
            if (!saveSecurity(state)) {
                onComplete(false)
                return
            }
        }
        if (screenId == "onb-profile-select") {
            selectProfile(button, state, onComplete)
            return
        }
        if (screenId == "onb-otp" || screenId == "onb-mail-otp") {
            verifyOtp(button, state, onComplete)
            return
        }
        if (screenId == "onb-status") {
            Log.d(logTag, "onboarding completed")
            navigator.appState.onboardingDone = true
        }
        super.primary(button, state, onComplete)
    }

    override fun action(action: String) {
        if (action == "resend" && (screenId == "onb-otp" || screenId == "onb-mail-otp")) {
            resendChallenge()
            return
        }
        super.action(action)
    }

    /** Validates the PIN pair, stores it and enables biometric unlock when a biometric toggle is on. */
    private fun saveSecurity(state: ScreenState): Boolean {
        val pin = state.inputValues["pin"].orEmpty()
        val confirmation = state.inputValues["confirmPin"].orEmpty()
        if (pin.length != PIN_LENGTH || !pin.all(Char::isDigit) || isTrivialPin(pin)) {
            showMessage("{{onb.error.pin_invalid}}")
            return false
        }
        if (pin != confirmation) {
            showMessage("{{onb.error.pin_mismatch}}")
            return false
        }
        val biometric = state.inputValues["faceIdUnlock"] == "true" || state.inputValues["fingerprintUnlock"] == "true"
        navigator.appState.apply {
            savePin(pin)
            biometricUnlockEnabled = biometric
        }
        Log.d(logTag, "pin saved, biometric unlock enabled=$biometric")
        return true
    }

    private fun isTrivialPin(pin: String): Boolean {
        val steps = pin.zipWithNext { a, b -> b - a }
        return steps.all { it == 0 } || steps.all { it == 1 } || steps.all { it == -1 }
    }

    private fun otpContactType() = if (screenId == "onb-mail-otp") ContactType.EMAIL else ContactType.PHONE

    private fun resendChallenge() {
        if (!NetworkStatus.hasValidatedInternet(requireContext())) {
            showMessage("{{onb.network.required.title}}", "{{onb.network.required.message}}")
            return
        }
        val applicationId = navigator.appState.registrationApplicationId
        if (applicationId.isNullOrBlank()) {
            showMessage("{{onb.error.registration_failed.title}}", "{{onb.error.registration_failed.message}}")
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    navigator.onboardingApi.issueContactChallenge(applicationId, otpContactType())
                }
                message(getString(R.string.code_resent))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "contact challenge resend failed", error)
                showMessage("{{onb.error.registration_failed.title}}", "{{onb.error.registration_failed.message}}")
            }
        }
    }

    private fun selectProfile(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        val applicationId = navigator.appState.registrationApplicationId
        if (applicationId.isNullOrBlank() || !NetworkStatus.hasValidatedInternet(requireContext())) {
            showMessage("{{onb.profile.unavailable}}")
            onComplete(false)
            return
        }
        val request = ProfileSelectionRequest(
            customerType = if (state.choice == 1) CustomerType.BUSINESS else CustomerType.INDIVIDUAL,
            taxResidence = if (state.inputValues["taxResidence"] == "1") "OTHER" else "CD",
        )
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { navigator.onboardingApi.selectProfile(applicationId, request) }
                val target = button.toChoice?.getOrNull(state.choice) ?: button.to
                if (target != null) navigate(target)
                onComplete(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "profile selection failed", error)
                showMessage("{{onb.profile.unavailable}}")
                onComplete(false)
            }
        }
    }

    private fun savePersonalProfile(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        val applicationId = navigator.appState.registrationApplicationId
        if (applicationId.isNullOrBlank() || !NetworkStatus.hasValidatedInternet(requireContext())) {
            showMessage("{{onb.network.required.title}}", "{{onb.network.required.message}}")
            onComplete(false)
            return
        }
        val values = state.inputValues
        fun text(key: String) = values[key].orEmpty().trim()
        val birthDate = parseBirthDate(text("dateOfBirth"))
        val required = listOf("surname", "givenNames", "placeOfBirth", "address", "city", "occupation").map(::text)
        if (birthDate == null || required.any(String::isEmpty)) {
            showMessage("{{onb.profile.unavailable}}")
            onComplete(false)
            return
        }
        val request = PersonalProfileRequest(
            surname = text("surname"),
            givenNames = text("givenNames"),
            dateOfBirth = birthDate,
            placeOfBirth = text("placeOfBirth"),
            sex = if (values["sex"] == "1") Sex.F else Sex.M,
            nationality = if (values["nationality"] == "1") "OTHER" else "CD",
            address = text("address"),
            cityOrCommune = text("city"),
            occupation = text("occupation"),
            sourceOfFunds = when (values["sourceOfFunds"]) {
                "0" -> SourceOfFunds.SALARY
                "1" -> SourceOfFunds.BUSINESS_INCOME
                else -> SourceOfFunds.OTHER
            },
            incomeBand = text("monthlyIncome").toDoubleOrNull()?.takeIf { it > 0 }?.let(::incomeBand),
        )
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { navigator.onboardingApi.savePersonalProfile(applicationId, request) }
                navigate(button.to ?: "onb-justif-domicile")
                onComplete(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "personal profile save failed", error)
                showMessage("{{onb.profile.unavailable}}")
                onComplete(false)
            }
        }
    }

    private fun submitApplication(button: Block, onComplete: (Boolean) -> Unit) {
        val applicationId = navigator.appState.registrationApplicationId
        if (applicationId.isNullOrBlank() || !NetworkStatus.hasValidatedInternet(requireContext())) {
            showMessage("{{onb.network.required.title}}", "{{onb.network.required.message}}")
            onComplete(false)
            return
        }
        val document = pickedFiles[ADDRESS_DOCUMENT_FIELD]
        val location = geoAddress.takeIf { document == null }
        if (document == null && location == null) {
            showMessage("{{onb.error.address_proof_required}}")
            onComplete(false)
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            var uploading = document != null
            try {
                withContext(Dispatchers.IO) {
                    if (document != null) {
                        uploadFile(applicationId, document, ADDRESS_PROOF_PURPOSE)
                        uploading = false
                    } else if (location != null) {
                        navigator.appState.saveCurrentAddress(location.address, location.latitude, location.longitude)
                    }
                    navigator.onboardingApi.submitApplication(applicationId)
                }
                navigate(button.to ?: "onb-status")
                onComplete(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "application submission failed (upload=$uploading)", error)
                showMessage(if (uploading) "{{onb.error.upload_failed}}" else "{{onb.profile.unavailable}}")
                onComplete(false)
            }
        }
    }

    private suspend fun uploadFile(applicationId: String, file: Uri, purpose: String): String {
        val resolver = requireContext().contentResolver
        val contentType = resolver.getType(file)?.takeIf { it in ALLOWED_PROOF_TYPES }
            ?: error("Unsupported document type")
        val bytes = resolver.openInputStream(file)?.use { it.readNBytes(MAX_PROOF_BYTES + 1) }
            ?: error("Cannot read the selected document")
        require(bytes.size <= MAX_PROOF_BYTES) { "Document is larger than 10 MB" }
        val session = navigator.onboardingApi.createUploadSession(
            applicationId,
            UploadSessionRequest(purpose = purpose, contentType = contentType),
        )
        navigator.onboardingApi.uploadDocument(session, bytes, contentType)
        return session.uploadSessionId ?: error("Upload session has no id")
    }

    private fun attachIdentityDocument(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        val applicationId = navigator.appState.registrationApplicationId
        if (applicationId.isNullOrBlank() || !NetworkStatus.hasValidatedInternet(requireContext())) {
            showMessage("{{onb.network.required.title}}", "{{onb.network.required.message}}")
            onComplete(false)
            return
        }
        val values = state.inputValues
        val documentNumber = values["documentNumber"].orEmpty().trim()
        val issueDate = parseBirthDate(values["issueDate"].orEmpty().trim())
        val expiryDate = parseBirthDate(values["expiryDate"].orEmpty().trim())
        val front = pickedFiles["frontDocument"]
        val selfie = pickedFiles["selfie"]
        val back = pickedFiles["backDocument"]
        val documentType = when (values["documentType"]) {
            "1" -> DocumentType.ONIP_NIN
            "2" -> DocumentType.BIOMETRIC_PASSPORT
            "3" -> DocumentType.RESIDENCE_PERMIT
            else -> DocumentType.CENI
        }
        // ISO dates compare lexicographically.
        if (documentNumber.isEmpty() || issueDate == null || expiryDate == null || expiryDate <= issueDate ||
            front == null || selfie == null
        ) {
            showMessage("{{onb.error.identity_incomplete}}")
            onComplete(false)
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            var uploading = true
            try {
                withContext(Dispatchers.IO) {
                    val frontId = uploadFile(applicationId, front, "DOCUMENT_FRONT")
                    val backId = back?.let { uploadFile(applicationId, it, "DOCUMENT_BACK") }
                    val selfieId = uploadFile(applicationId, selfie, "LIVENESS_SELFIE")
                    uploading = false
                    navigator.onboardingApi.attachIdentityDocument(
                        applicationId,
                        IdentityDocumentRequest(
                            documentType = documentType,
                            documentNumber = documentNumber,
                            issuingCountry = "CD",
                            issueDate = issueDate,
                            expiryDate = expiryDate,
                            frontUploadId = frontId,
                            backUploadId = backId,
                            selfieUploadId = selfieId,
                        ),
                    )
                }
                navigate(button.to ?: "onb-infos-perso")
                onComplete(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "identity document failed (upload=$uploading)", error)
                showMessage(if (uploading) "{{onb.error.upload_failed}}" else "{{onb.error.identity_failed}}")
                onComplete(false)
            }
        }
    }

    override fun toggleChanged(fieldId: String, checked: Boolean, accept: (Boolean) -> Unit) {
        if (fieldId != GEOLOCATION_FIELD) {
            accept(true)
            return
        }
        if (!checked) {
            geoAddress = null
            accept(true)
            return
        }
        if (hasLocationPermission()) {
            locate(accept)
        } else {
            pendingGeolocation = accept
            locationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun hasPermission(permission: String) =
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    private fun hasLocationPermission() =
        hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) || hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun geolocationFailed(accept: (Boolean) -> Unit) {
        geoAddress = null
        showMessage("{{onb.error.geolocation_unavailable}}")
        accept(false)
    }

    @SuppressLint("MissingPermission")
    private fun locate(accept: (Boolean) -> Unit) {
        val manager = requireContext().getSystemService(LocationManager::class.java)
        val providers = buildList {
            if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) add(LocationManager.GPS_PROVIDER)
            add(LocationManager.NETWORK_PROVIDER)
        }
        val provider = providers.firstOrNull { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
        if (provider == null) {
            geolocationFailed(accept)
            return
        }
        manager.getCurrentLocation(provider, null, ContextCompat.getMainExecutor(requireContext())) { location ->
            if (!isAdded) return@getCurrentLocation
            if (location == null) geolocationFailed(accept) else resolveAddress(location, accept)
        }
    }

    private fun resolveAddress(location: Location, accept: (Boolean) -> Unit) {
        fun done(address: String?) {
            if (!isAdded) return
            requireActivity().runOnUiThread {
                val coordinates = String.format(Locale.ROOT, "%.6f, %.6f", location.latitude, location.longitude)
                geoAddress = GeoAddress(address?.takeIf(String::isNotBlank) ?: coordinates, location.latitude, location.longitude)
                accept(true)
            }
        }
        if (!Geocoder.isPresent()) {
            done(null)
            return
        }
        Geocoder(requireContext(), Locale.forLanguageTag(navigator.appState.languageCode)).getFromLocation(
            location.latitude,
            location.longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) = done(addresses.firstOrNull()?.getAddressLine(0))
                override fun onError(errorMessage: String?) = done(null)
            },
        )
    }

    /** The date picker writes dd/MM/yyyy; the API expects an ISO date. */
    private fun parseBirthDate(value: String): String? {
        val parts = value.split('/')
        if (parts.size != 3) return null
        val (day, month, year) = parts.map { it.toIntOrNull() ?: return null }
        return String.format(Locale.ROOT, "%04d-%02d-%02d", year, month, day)
    }

    private fun incomeBand(monthlyIncome: Double) = when {
        monthlyIncome < 200_000 -> IncomeBand.BAND_0_200K
        monthlyIncome < 1_000_000 -> IncomeBand.BAND_200K_1M
        monthlyIncome < 5_000_000 -> IncomeBand.BAND_1M_5M
        else -> IncomeBand.BAND_5M_PLUS
    }

    private fun verifyOtp(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        if (!NetworkStatus.hasValidatedInternet(requireContext())) {
            showMessage("{{onb.network.required.title}}", "{{onb.network.required.message}}")
            onComplete(false)
            return
        }
        val isEmail = otpContactType() == ContactType.EMAIL
        val code = if (isEmail) {
            state.inputValues["emailCode"].orEmpty().filter { !it.isWhitespace() }
        } else {
            state.inputValues["otp"].orEmpty().filter(Char::isDigit)
        }
        if (code.length != if (isEmail) EMAIL_CODE_LENGTH else OTP_LENGTH) {
            showMessage(if (isEmail) "{{onb.error.mail_otp_invalid}}" else "{{onb.error.otp_invalid}}")
            onComplete(false)
            return
        }
        val applicationId = navigator.appState.registrationApplicationId
        if (applicationId.isNullOrBlank()) {
            showMessage("{{onb.error.registration_failed.title}}", "{{onb.error.registration_failed.message}}")
            onComplete(false)
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val contactType = otpContactType()
                    navigator.onboardingApi.verifyContact(
                        applicationId,
                        contactType,
                        if (contactType == ContactType.EMAIL) VerifyContactRequest(token = code) else VerifyContactRequest(code = code),
                    )
                    if (contactType == ContactType.PHONE) {
                        // Best effort: the e-mail screen offers Resend if this challenge is not delivered.
                        try {
                            navigator.onboardingApi.issueContactChallenge(applicationId, ContactType.EMAIL)
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (error: Exception) {
                            Log.w(logTag, "email contact challenge failed", error)
                        }
                    }
                }
                navigate(button.to ?: if (screenId == "onb-otp") "onb-mail-otp" else "onb-auth-security")
                onComplete(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "contact verification failed", error)
                showMessage(if (isEmail) "{{onb.error.mail_otp_invalid}}" else "{{onb.error.otp_invalid}}")
                onComplete(false)
            }
        }
    }

    private fun register(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        if (!NetworkStatus.hasValidatedInternet(requireContext())) {
            showMessage("{{onb.network.required.title}}", "{{onb.network.required.message}}")
            onComplete(false)
            return
        }
        if (!state.termsAccepted) {
            showMessage("{{onb.error.terms_required}}")
            onComplete(false)
            return
        }
        val email = state.inputValues["email"].orEmpty().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage("{{onb.error.email_invalid}}")
            onComplete(false)
            return
        }
        val phoneDigits = state.inputValues["phoneNumber"].orEmpty().filter(Char::isDigit)
        val phoneNumber = when {
            phoneDigits.length == 9 -> "+243$phoneDigits"
            phoneDigits.length == 12 && phoneDigits.startsWith("243") -> "+$phoneDigits"
            else -> {
                showMessage("{{onb.error.phone_invalid}}")
                onComplete(false)
                return
            }
        }
        val appState = navigator.appState
        val request = RegisterRequest(
            phoneNumber = phoneNumber,
            email = email,
            locale = "${appState.languageCode}-CD",
            terms = TermsRequest(
                termsVersion = TERMS_VERSION,
                privacyPolicyVersion = PRIVACY_VERSION,
                accepted = state.termsAccepted,
            ),
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // IDs from the former offline fallback were never registered with the API.
                    val previousId = appState.registrationApplicationId?.takeUnless { it.startsWith(DEMO_ID_PREFIX) }
                    val sameContacts = appState.registrationPhoneNumber == phoneNumber &&
                        appState.registrationEmail == email
                    val applicationId = if (previousId != null && sameContacts) {
                        previousId
                    } else {
                        val registered = navigator.onboardingApi.registerApplication(request)
                        val newId = registered.applicationId
                            ?.takeIf(String::isNotBlank)
                            ?: error("Registration response did not include an application ID")
                        appState.registrationApplicationId = newId
                        appState.registrationPhoneNumber = phoneNumber
                        appState.registrationEmail = email
                        // Close the application registered with the old contacts; best effort,
                        // since it may already be expired or withdrawn.
                        if (previousId != null) {
                            try {
                                navigator.onboardingApi.withdrawApplication(previousId)
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (error: Exception) {
                                Log.w(logTag, "could not withdraw previous application", error)
                            }
                        }
                        newId
                    }
                    navigator.onboardingApi.issueContactChallenge(applicationId, ContactType.PHONE)
                }
                navigate(button.to ?: "onb-otp")
                onComplete(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "application registration or contact challenge failed", error)
                showMessage("{{onb.error.registration_failed.title}}", "{{onb.error.registration_failed.message}}")
                onComplete(false)
            }
        }
    }

    private fun showMessage(titleKey: String, messageKey: String? = null) {
        val language = currentLanguage()
        AlertDialog.Builder(requireContext())
            .setTitle(ScreenLabels.resolve(titleKey, language))
            .apply {
                if (messageKey != null) setMessage(ScreenLabels.resolve(messageKey, language))
            }
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private companion object {
        const val TERMS_VERSION = "2026-09-01"
        const val PRIVACY_VERSION = "2026-09-01"
        const val DEMO_ID_PREFIX = "demo-"
        const val OTP_LENGTH = 6
        const val EMAIL_CODE_LENGTH = 64
        const val PIN_LENGTH = 4
        const val ADDRESS_DOCUMENT_FIELD = "addressDocument"
        const val GEOLOCATION_FIELD = "useGeolocation"
        const val ADDRESS_PROOF_PURPOSE = "ADDRESS_PROOF"
        const val MAX_PROOF_BYTES = 10 * 1024 * 1024
        val ALLOWED_PROOF_TYPES = setOf("image/jpeg", "image/png", "application/pdf")
    }
}
