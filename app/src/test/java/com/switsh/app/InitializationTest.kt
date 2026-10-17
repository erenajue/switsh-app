package com.switsh.app

import com.google.gson.Gson
import com.switsh.app.model.ScreenCatalog
import com.switsh.app.model.ScreenLabels
import java.io.File
import org.junit.Assert.assertNotNull
import org.junit.Test

class InitializationTest {
    @Test
    fun testAllLanguagesInScreenLabels() {
        val languages = listOf("fr", "en", "ln", "kg", "lua", "sw")
        for (key in ScreenLabels.keys) {
            for (lang in languages) {
                val resolved = ScreenLabels.resolve("{{$key}}", lang)
                assertNotNull("Missing resolution for key $key in $lang", resolved)
            }
        }
    }

    @Test
    fun testClassInitializationAndLocalization() {
        val catalog = File("src/main/assets/screens.json").reader().use {
            Gson().fromJson(it, ScreenCatalog::class.java)
        }
        val languages = listOf("fr", "en", "ln", "kg", "lua", "sw")
        for (screen in catalog.screens) {
            for (lang in languages) {
                val localized = ScreenLabels.localize(screen, lang)
                assertNotNull(localized)
            }
        }
    }
}
