package com.switsh.app.screens

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.switsh.app.R
import com.switsh.app.model.Block
import com.switsh.app.model.Screen
import com.switsh.app.model.ScreenLabels
import com.switsh.app.ui.BlockRenderer
import com.switsh.app.ui.RenderActions
import com.switsh.app.ui.ScreenState
import com.switsh.app.ui.UiKit

/**
 * Base fragment for a prototype screen: draws the chrome (header, step bar, scroll area,
 * pinned CTA, tab bar) around the blocks of [screenId]. Sections subclass it to add their own rules.
 */
open class ScreenFragment : Fragment(), RenderActions {
    protected val navigator: Navigator get() = requireActivity() as Navigator
    protected val screenId: String get() = requireArguments().getString(ARG_SCREEN_ID).orEmpty()
    protected val logTag: String get() = "Switsh/${javaClass.simpleName}"
    protected open val lightSystemBars: Boolean = true
    private lateinit var ui: UiKit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        ui = UiKit(requireContext())
        val screen = navigator.catalog.find(screenId)?.let {
            ScreenLabels.localize(it, navigator.appState.languageCode)
        }
        Log.d(logTag, "show $screenId")
        if (screen == null) {
            Log.w(logTag, "unknown screen $screenId")
            return View(requireContext())
        }
        return buildPage(screen)
    }

    override fun onResume() {
        super.onResume()
        val window = requireActivity().window
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = lightSystemBars
            isAppearanceLightNavigationBars = lightSystemBars
        }
    }

    protected open fun buildPage(screen: Screen): View {
        val rendered = BlockRenderer(requireContext(), this).render(screen)
        val hasHeader = screen.chrome == "sub"
        val page = ui.vertical().apply { setBackgroundColor(ui.color(R.color.bg)) }
        ViewCompat.setOnApplyWindowInsetsListener(page) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(0, bars.top, 0, bars.bottom)
            insets
        }

        if (hasHeader) page.addView(header(screen), ui.lp())
        val scroll = ScrollView(requireContext()).apply { clipToPadding = false }
        rendered.content.setPadding(ui.dp(20), ui.dp(if (hasHeader) 8 else 24), ui.dp(20), ui.dp(24))
        scroll.addView(rendered.content, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        page.addView(scroll, ui.lp(height = 0, weight = 1f))

        rendered.cta?.let { cta ->
            val bar = ui.vertical().apply {
                setBackgroundColor(ui.color(R.color.surface))
                setPadding(ui.dp(20), ui.dp(12), ui.dp(20), ui.dp(12))
                addView(cta)
            }
            page.addView(divider(), ui.lp(height = ui.dp(1)))
            page.addView(bar, ui.lp())
        }
        screen.tab?.let {
            page.addView(divider(), ui.lp(height = ui.dp(1)))
            page.addView(tabBar(it), ui.lp())
        }
        return page
    }

    private fun divider() = View(requireContext()).apply { setBackgroundColor(ui.color(R.color.divider)) }

    private fun header(screen: Screen): View {
        val box = ui.vertical().apply { setBackgroundColor(ui.color(R.color.surface)) }
        val row = ui.horizontal().apply { setPadding(ui.dp(8), ui.dp(8), ui.dp(16), ui.dp(8)) }
        if (parentFragmentManager.backStackEntryCount > 0) {
            row.addView(
                FrameLayout(requireContext()).apply {
                    contentDescription = getString(R.string.back)
                    ui.ripple(this)
                    setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
                    addView(ui.icon("chevron_left", 24, R.color.text), FrameLayout.LayoutParams(ui.dp(24), ui.dp(24), Gravity.CENTER))
                },
                ui.lp(width = ui.dp(44), height = ui.dp(44)),
            )
        } else {
            row.addView(View(requireContext()), ui.lp(width = ui.dp(8), height = ui.dp(44)))
        }
        row.addView(ui.text(screen.title, 17f, bold = true), ui.lp(width = 0, weight = 1f))
        screen.step?.let { row.addView(ui.text("${it[0]}/${it[1]}", 12f, R.color.text_muted, bold = true)) }
        box.addView(row, ui.lp())
        screen.step?.takeIf { it.size == 2 }?.let { step ->
            val bar = ui.horizontal().apply { setPadding(ui.dp(20), 0, ui.dp(20), ui.dp(10)) }
            repeat(step[1]) { index ->
                bar.addView(
                    View(requireContext()).apply { background = ui.rounded(if (index < step[0]) R.color.primary else R.color.surface_offset2, 3) },
                    ui.lp(width = 0, height = ui.dp(4), weight = 1f, start = if (index == 0) 0 else 4),
                )
            }
            box.addView(bar, ui.lp())
        }
        return box
    }

    private fun tabBar(activeId: String): View = ui.horizontal().apply {
        setBackgroundColor(ui.color(R.color.surface))
        setPadding(0, ui.dp(6), 0, ui.dp(6))
        TABS.forEach { tab ->
            val tint = if (tab.id == activeId) R.color.primary else R.color.text_faint
            val cell = ui.vertical().apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(0, ui.dp(4), 0, ui.dp(4))
                ui.ripple(this)
                setOnClickListener { navigator.reset(tab.root) }
            }
            cell.addView(ui.icon(tab.icon, 24, tint), ui.lp(width = ui.dp(24), height = ui.dp(24), bottom = 2).apply { gravity = Gravity.CENTER_HORIZONTAL })
            cell.addView(ui.text(getString(tab.label), 11f, tint, bold = tab.id == activeId).apply { gravity = Gravity.CENTER })
            addView(cell, ui.lp(width = 0, weight = 1f))
        }
    }

    // --- RenderActions -------------------------------------------------------------------

    override fun navigate(id: String) {
        Log.d(logTag, "navigate $screenId -> $id")
        if (navigator.catalog.find(id) == null) {
            message(getString(R.string.coming_soon))
            return
        }
        if (id in TAB_ROOTS || id == "start-unlock") navigator.reset(id) else navigator.open(id)
    }

    override fun primary(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit) {
        val target = button.toChoice?.getOrNull(state.choice) ?: button.to
        if (target != null) {
            navigate(target)
            onComplete(true)
        } else {
            message(getString(R.string.saved_example))
            if (parentFragmentManager.backStackEntryCount > 0) requireActivity().onBackPressedDispatcher.onBackPressed()
            onComplete(false)
        }
    }

    override fun action(action: String) {
        Log.d(logTag, "action $action on $screenId")
        when (action) {
            "logout" -> navigator.reset("start-unlock")
            "resend" -> message(getString(R.string.code_resent))
            else -> message(getString(R.string.example_only))
        }
    }

    override fun message(text: String) {
        view?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    // Placeholder authentication: any complete PIN (or the biometric key) unlocks the demo account.
    override fun pinEntered() = navigate("bank-accounts")

    override fun currentLanguage(): String = navigator.appState.languageCode

    class Tab(val id: String, val label: Int, val icon: String, val root: String)

    companion object {
        const val ARG_SCREEN_ID = "screenId"
        val TABS = listOf(
            Tab("accounts", R.string.tab_accounts, "home", "bank-accounts"),
            Tab("cards", R.string.tab_cards, "card", "card-physical"),
            Tab("payments", R.string.tab_payments, "send", "pay-p2p"),
            Tab("pfm", R.string.tab_pfm, "piggy", "pfm-budget"),
            Tab("more", R.string.tab_more, "gear", "settings-profile"),
        )
        val TAB_ROOTS = TABS.map { it.root }.toSet()

        /** Picks the fragment of the section that owns [screenId]. */
        fun forScreen(screenId: String, group: String?): ScreenFragment {
            val fragment = when {
                screenId == "start-splash" -> SplashFragment()
                group == "start" -> StartFragment()
                group == "onboarding" -> OnboardingFragment()
                else -> ScreenFragment()
            }
            return fragment.apply { arguments = bundleOf(ARG_SCREEN_ID to screenId) }
        }
    }
}
