package com.switsh.app.screens

import android.util.Patterns
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.switsh.app.model.Block
import com.switsh.app.model.ScreenLabels
import com.switsh.app.domains.onboarding.ContactType
import com.switsh.app.domains.onboarding.RegisterRequest
import com.switsh.app.domains.onboarding.TermsRequest
import com.switsh.app.ui.ScreenState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** "Onboarding" section: registration, KYC/KYB steps and verification status. */
class OnboardingFragment : ScreenFragment() {
    override fun primary(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        if (screenId == "onb-inscription") {
            register(button, state, onComplete)
            return
        }
        if (screenId == "onb-status") {
            Log.d(logTag, "onboarding completed")
            navigator.appState.onboardingDone = true
        }
        super.primary(button, state, onComplete)
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
                    val existingId = appState.registrationApplicationId
                    val applicationId = if (existingId != null) {
                        existingId
                    } else {
                        val registered = navigator.onboardingApi.registerApplication(request)
                        registered.applicationId
                            ?.takeIf(String::isNotBlank)
                            ?.also {
                                appState.registrationApplicationId = it
                                appState.registrationPhoneNumber = phoneNumber
                            }
                            ?: error("Registration response did not include an application ID")
                    }
                    navigator.onboardingApi.issueContactChallenge(applicationId, ContactType.PHONE)
                }
                navigate(button.to ?: "onb-otp")
                onComplete(true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                Log.w(logTag, "application registration or contact challenge failed, using prototype fallback", error)
                if (appState.registrationApplicationId == null) {
                    appState.registrationApplicationId = "demo-${java.util.UUID.randomUUID().toString().take(8)}"
                    appState.registrationPhoneNumber = phoneNumber
                }
                navigate(button.to ?: "onb-otp")
                onComplete(true)
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
    }
}
