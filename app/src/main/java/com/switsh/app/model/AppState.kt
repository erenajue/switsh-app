package com.switsh.app.model

import android.content.Context
import com.switsh.app.data.LastConnectionDatabase
import java.util.Base64
import java.util.UUID

/** First-connection / registration state kept on the device. */
class AppState(context: Context) {
    private val prefs = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    private val lastConnectionDatabase = LastConnectionDatabase(context.applicationContext)

    var languageCode: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).putBoolean(KEY_LANGUAGE_CHOSEN, true).apply()

    val languageChosen: Boolean get() = prefs.getBoolean(KEY_LANGUAGE_CHOSEN, false)

    var registrationApplicationId: String?
        get() = prefs.getString(KEY_REGISTRATION_APPLICATION_ID, null)
        set(value) = prefs.edit().putString(KEY_REGISTRATION_APPLICATION_ID, value).apply()

    var registrationOnboardingId: String?
        get() = prefs.getString(KEY_REGISTRATION_ONBOARDING_ID, null)
        set(value) = prefs.edit().putString(KEY_REGISTRATION_ONBOARDING_ID, value).apply()

    var registrationPhoneNumber: String?
        get() = prefs.getString(KEY_REGISTRATION_PHONE_NUMBER, null)
        set(value) = prefs.edit().putString(KEY_REGISTRATION_PHONE_NUMBER, value).apply()

    var registrationEmail: String?
        get() = prefs.getString(KEY_REGISTRATION_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_REGISTRATION_EMAIL, value).apply()

    var onboardingAccessToken: String?
        get() = prefs.getString(KEY_ONBOARDING_ACCESS_TOKEN, null) ?: getOrGenerateOnboardingAccessToken()
        set(value) = prefs.edit().putString(KEY_ONBOARDING_ACCESS_TOKEN, value).apply()

    fun getOrGenerateOnboardingAccessToken(): String {
        val existing = prefs.getString(KEY_ONBOARDING_ACCESS_TOKEN, null)?.takeIf(String::isNotBlank)
        if (existing != null) return existing
        val newToken = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_ONBOARDING_ACCESS_TOKEN, newToken).apply()
        return newToken
    }

    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    val hasPin: Boolean get() = prefs.getString(KEY_PIN_HASH, null) != null

    var biometricUnlockEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_UNLOCK, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_UNLOCK, value).apply()

    /** Stores only a salted BCrypt hash of the PIN, never the PIN itself. */
    fun savePin(pin: String) {
        prefs.edit()
            .putString(KEY_PIN_HASH, PinHasher.hash(pin))
            .remove(KEY_PIN_SALT)
            .apply()
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val verification = PinHasher.verify(
            pin = pin,
            storedHash = storedHash,
            legacySalt = prefs.getString(KEY_PIN_SALT, null),
        )
        if (verification.valid && verification.upgradedHash != null) {
            prefs.edit()
                .putString(KEY_PIN_HASH, verification.upgradedHash)
                .remove(KEY_PIN_SALT)
                .apply()
        }
        return verification.valid
    }

    val lastConnectedName: String?
        get() = lastConnectionDatabase.getLastConnectedName()

    fun saveProfileName(givenNames: String, surname: String) {
        lastConnectionDatabase.saveProfileName(
            listOf(givenNames.trim(), surname.trim()).filter(String::isNotEmpty).joinToString(" "),
        )
    }

    fun recordSuccessfulConnection() {
        lastConnectionDatabase.recordSuccessfulConnection()
    }

    fun saveCurrentAddress(address: String, latitude: Double, longitude: Double) {
        lastConnectionDatabase.saveCurrentAddress(address, latitude, longitude)
    }

    val currentAddress: String? get() = lastConnectionDatabase.getCurrentAddress()

    fun close() = lastConnectionDatabase.close()

    companion object {
        const val DEFAULT_LANGUAGE = "fr"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_LANGUAGE_CHOSEN = "language_chosen"
        private const val KEY_REGISTRATION_APPLICATION_ID = "registration_application_id"
        private const val KEY_REGISTRATION_ONBOARDING_ID = "registration_onboarding_id"
        private const val KEY_REGISTRATION_PHONE_NUMBER = "registration_phone_number"
        private const val KEY_REGISTRATION_EMAIL = "registration_email"
        private const val KEY_ONBOARDING_ACCESS_TOKEN = "onboarding_access_token"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_BIOMETRIC_UNLOCK = "biometric_unlock_enabled"
    }
}
