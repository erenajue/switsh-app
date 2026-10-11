package com.switsh.app.model

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScreenLabelsTest {
    @Test
    fun generatedOnboardingLabelsExistInEverySupportedLanguage() {
        val screens = File("src/main/assets/screens.json").readText()
        val keys = Regex("""\{\{([a-z0-9_.-]+)}}""")
            .findAll(screens)
            .map { it.groupValues[1] }
            .toSet()

        assertTrue(keys.isNotEmpty())
        assertTrue(keys.all { it in ScreenLabels.keys })
        listOf("fr", "en", "ln", "kg", "lua", "sw").forEach { language ->
            ScreenLabels.keys.forEach { key ->
                assertFalse(
                    "Unresolved label '$key' for language '$language'",
                    ScreenLabels.resolve("{{$key}}", language)!!.contains("{{"),
                )
            }
        }
    }

    @Test
    fun selectedLanguageChoosesTheMatchingLabel() {
        assertEquals("Create your account", ScreenLabels.resolve("{{onb.registration.title}}", "en"))
        assertEquals("Créer votre compte", ScreenLabels.resolve("{{onb.registration.title}}", "fr"))
        assertEquals("Créer votre compte", ScreenLabels.resolve("{{onb.registration.title}}", "unknown"))
    }

    @Test
    fun localizesScreenTitlesAndNestedBlocks() {
        val catalog = File("src/main/assets/screens.json").reader().use {
            com.google.gson.Gson().fromJson(it, ScreenCatalog::class.java)
        }
        val source = catalog.find("onb-inscription")!!
        val english = ScreenLabels.localize(source, "en")

        assertEquals("Registration", english.title)
        assertEquals("Create your account", english.blocks.first().text)
        assertEquals("{{onb.registration.screen_title}}", source.title)
    }
}
