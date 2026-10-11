package com.switsh.app.screens

import android.util.Log
import com.switsh.app.model.Block
import com.switsh.app.ui.ScreenState

/** "Onboarding" section: registration, KYC/KYB steps and verification status. */
class OnboardingFragment : ScreenFragment() {
    override fun primary(button: Block, state: ScreenState) {
        if (screenId == "onb-status") {
            Log.d(logTag, "onboarding completed")
            navigator.appState.onboardingDone = true
        }
        super.primary(button, state)
    }
}
