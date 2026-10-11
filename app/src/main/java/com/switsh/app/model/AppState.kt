package com.switsh.app.model

import android.content.Context

/** First-connection / registration state kept on the device. */
class AppState(context: Context) {
    private val prefs = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    var languageCode: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).putBoolean(KEY_LANGUAGE_CHOSEN, true).apply()

    val languageChosen: Boolean get() = prefs.getBoolean(KEY_LANGUAGE_CHOSEN, false)

    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    companion object {
        const val DEFAULT_LANGUAGE = "fr"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_LANGUAGE_CHOSEN = "language_chosen"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
    }
}
