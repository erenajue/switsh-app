package com.switsh.app.screens

import android.util.Log
import android.view.View
import com.switsh.app.model.Block
import com.switsh.app.model.Screen
import com.switsh.app.model.ScreenLabels
import com.switsh.app.ui.ScreenState

/** "Démarrage" section: language choice and PIN unlock. */
class StartFragment : ScreenFragment() {
    override fun buildPage(screen: Screen): View {
        if (screen.id != "start-unlock") return super.buildPage(screen)

        val name = navigator.appState.lastConnectedName
        val titleKey = if (name.isNullOrBlank()) {
            "{{start.unlock.welcome.generic}}"
        } else {
            "{{start.unlock.welcome}}"
        }
        val welcome = ScreenLabels.resolve(titleKey, navigator.appState.languageCode)
            .orEmpty()
            .replace("%s", name.orEmpty())
        val personalized = screen.copy(
            blocks = screen.blocks.map { block ->
                if (block.t == "pinpad") block.copy(name = name, title = welcome) else block
            },
        )
        return super.buildPage(personalized)
    }

    override fun primary(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        if (screenId == "start-language") {
            if (!NetworkStatus.hasValidatedInternet(requireContext())) {
                navigator.reset("start-offline")
                onComplete(false)
                return
            }
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
        super.primary(button, state, onComplete)
    }
}
