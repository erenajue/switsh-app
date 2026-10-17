package com.switsh.app.model

import android.content.Context
import com.google.gson.Gson

data class ScreenCatalog(val groups: List<ScreenGroup>, val screens: List<Screen>) {
    // Gson creates this class without running its constructor, so no property initializers or lazy delegates here.
    fun find(id: String): Screen? = screens.firstOrNull { it.id == id }
}

data class ScreenGroup(val id: String, val label: String)

data class Screen(
    val id: String,
    val group: String,
    val title: String,
    val subtitle: String? = null,
    val chrome: String,
    val tab: String? = null,
    val step: List<Int>? = null,
    val blocks: List<Block> = emptyList(),
)

data class Item(
    val name: String? = null,
    val code: String? = null,
    val cur: String? = null,
    val style: String? = null,
    val label: String? = null,
    val amount: String? = null,
    val icon: String? = null,
    val to: String? = null,
)

data class Trailing(
    val kind: String,
    val text: String? = null,
    val style: String? = null,
    val on: Boolean = false,
    val bold: Boolean = false,
)

/** One UI building block; [t] selects the renderer, other fields are optional. */
data class Block(
    val t: String,
    val text: String? = null,
    val title: String? = null,
    val sub: String? = null,
    val icon: String? = null,
    val label: String? = null,
    val kind: String? = null,
    val style: String? = null,
    val small: Boolean = false,
    val required: String? = null,
    val ui: String? = null,
    val prefix: String? = null,
    val fieldId: String? = null,
    val placeholder: String? = null,
    val hint: String? = null,
    val options: List<String>? = null,
    val length: Int = 0,
    val on: Boolean = false,
    val done: Boolean = false,
    val selected: Boolean = false,
    val checked: Boolean = false,
    val tint: String? = null,
    val lead: String? = null,
    val number: String? = null,
    val holder: String? = null,
    val extra: String? = null,
    val badge: String? = null,
    val name: String? = null,
    val hello: String? = null,
    val forgot: String? = null,
    val tag: String? = null,
    val amount: String? = null,
    val percent: Double = 0.0,
    val from: String? = null,
    val active: Int = 0,
    val actives: List<Int>? = null,
    val link: String? = null,
    val to: String? = null,
    val toChoice: List<String>? = null,
    val targets: List<String>? = null,
    val action: String? = null,
    val bare: Boolean = false,
    val children: List<Block>? = null,
    val items: List<Item>? = null,
    val trailing: Trailing? = null,
)

object ScreenRepository {
    fun load(context: Context): ScreenCatalog =
        context.assets.open("screens.json").bufferedReader().use {
            Gson().fromJson(it, ScreenCatalog::class.java)
        }
}
