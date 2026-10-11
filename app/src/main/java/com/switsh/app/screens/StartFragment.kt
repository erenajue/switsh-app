package com.switsh.app.screens

import android.util.Log
import com.switsh.app.model.Block
import com.switsh.app.ui.ScreenState

/** "Démarrage" section: language choice and PIN unlock. */
class StartFragment : ScreenFragment() {
    override fun primary(button: Block, state: ScreenState) {
        if (screenId == "start-language") {
            state.languageCode?.let {
                Log.d(logTag, "language saved: $it")
                navigator.appState.languageCode = it
            }
        }
        val target = button.to
        if (screenId == "start-language" && target != null) {
            navigator.reset(target)
            return
        }
        super.primary(button, state)
    }
}
