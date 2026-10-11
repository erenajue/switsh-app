package com.switsh.app.model

import com.google.gson.Gson
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ScreenCatalogTest {
    private val catalog = File("src/main/assets/screens.json").reader().use {
        Gson().fromJson(it, ScreenCatalog::class.java)
    }

    @Test
    fun findsScreensAfterGsonParsing() {
        assertNotNull(catalog.find("start-language"))
        assertEquals("onb-inscription", catalog.find("onb-inscription")?.id)
    }

    @Test
    fun languageContinuesToOnboarding() {
        val cta = catalog.find("start-language")!!.blocks.first { it.t == "cta" }
        assertEquals("onb-inscription", cta.children!!.first().to)
    }
}
