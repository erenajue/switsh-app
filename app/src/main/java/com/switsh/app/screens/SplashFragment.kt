package com.switsh.app.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.switsh.app.R
import com.switsh.app.model.Screen

/** "Démarrage › Écran de lancement": shown briefly, then presumes a first connection (language choice, then onboarding) until onboarding is completed. */
class SplashFragment : ScreenFragment() {
    override val lightSystemBars = false
    private val handler = Handler(Looper.getMainLooper())

    override fun buildPage(screen: Screen): View = layoutInflater.inflate(R.layout.screen_splash, null)

    override fun onResume() {
        super.onResume()
        handler.postDelayed({
            val next = if (NetworkStatus.hasValidatedInternet(requireContext())) firstScreen() else "start-offline"
            Log.d(logTag, "splash done -> $next")
            navigator.reset(next)
        }, SPLASH_MS)
    }

    override fun onPause() {
        handler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    private fun firstScreen(): String = with(navigator.appState) {
        when {
            onboardingDone -> "start-unlock"
            !languageChosen -> "start-language"
            else -> "onb-inscription"
        }
    }

    private companion object {
        const val SPLASH_MS = 1600L
    }
}
