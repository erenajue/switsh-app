package com.switsh.app.screens

import com.switsh.app.model.AppState
import com.switsh.app.model.ScreenCatalog

/** Navigation and shared state offered by the host activity to every screen fragment. */
interface Navigator {
    val appState: AppState
    val catalog: ScreenCatalog

    /** Opens a screen on top of the current one (back returns to it). */
    fun open(screenId: String)

    /** Replaces the whole back stack with a single screen. */
    fun reset(screenId: String)
}
