package com.switsh.app

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.switsh.app.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedLanguageCode = FRENCH_CODE

    private val availableLanguages = listOf(
        Language("Français", "FR", FRENCH_CODE),
        Language("English", "EN", "en"),
        Language("Lingála", "LN", "ln"),
        Language("Kikongo", "KG", "kg"),
        Language("Tshiluba", "LUA", "lua"),
        Language("Kiswahili", "SW", "sw"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, binding.root).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        val initialPaddingLeft = binding.root.paddingLeft
        val initialPaddingTop = binding.root.paddingTop
        val initialPaddingRight = binding.root.paddingRight
        val initialPaddingBottom = binding.root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                initialPaddingLeft + insets.left,
                initialPaddingTop + insets.top,
                initialPaddingRight + insets.right,
                initialPaddingBottom + insets.bottom,
            )
            windowInsets
        }

        selectedLanguageCode = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
            .getString(LANGUAGE_KEY, FRENCH_CODE)
            ?.takeIf { code -> availableLanguages.any { it.code == code } }
            ?: FRENCH_CODE
        renderLanguageOptions()

        binding.continueButton.setOnClickListener {
            getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putString(LANGUAGE_KEY, selectedLanguageCode)
                .apply()
            Snackbar.make(
                binding.root,
                getString(R.string.language_saved),
                Snackbar.LENGTH_SHORT,
            ).show()
        }
    }

    private fun renderLanguageOptions() {
        binding.languageOptions.removeAllViews()
        availableLanguages.forEach { language ->
            binding.languageOptions.addView(createLanguageOption(language))
        }
    }

    private fun createLanguageOption(language: Language): View {
        val selected = language.code == selectedLanguageCode
        val card = MaterialCardView(this).apply {
            radius = dp(16).toFloat()
            cardElevation = 0f
            strokeWidth = dp(1.5f)
            strokeColor = getColor(if (selected) R.color.switsh_teal else R.color.border)
            setCardBackgroundColor(
                getColor(if (selected) R.color.switsh_teal_light else R.color.surface_card),
            )
            isClickable = true
            isFocusable = true
            useCompatPadding = false
            contentDescription = getString(
                if (selected) R.string.language_option_selected else R.string.language_option,
                language.name,
            )
            setOnClickListener {
                selectedLanguageCode = language.code
                renderLanguageOptions()
            }
        }
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(60),
        ).apply {
            bottomMargin = dp(8)
        }
        card.layoutParams = layoutParams

        ViewCompat.setAccessibilityDelegate(card, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat,
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.className = "android.widget.RadioButton"
                info.isCheckable = true
                info.isChecked = language.code == selectedLanguageCode
            }
        })

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), 0, dp(16), 0)
        }
        card.addView(
            row,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
            ),
        )

        val languageLabel = TextView(this).apply {
            text = language.name
            textSize = 15f
            setTextColor(getColor(R.color.text_primary))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        row.addView(
            languageLabel,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
        )

        val languageCode = TextView(this).apply {
            text = language.displayCode
            textSize = 11f
            setTextColor(getColor(R.color.text_faint))
        }
        row.addView(languageCode)

        val check = TextView(this).apply {
            gravity = Gravity.CENTER
            text = if (selected) CHECK_MARK else ""
            textSize = 14f
            setTextColor(getColor(android.R.color.white))
            background = getDrawable(
                if (selected) R.drawable.language_check_selected
                else R.drawable.language_check_unselected,
            )
        }
        row.addView(
            check,
            LinearLayout.LayoutParams(dp(22), dp(22)).apply {
                marginStart = dp(12)
            },
        )

        return card
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun dp(value: Float): Int =
        (value * resources.displayMetrics.density).roundToInt()

    private data class Language(
        val name: String,
        val displayCode: String,
        val code: String,
    )

    private companion object {
        const val PREFERENCES_NAME = "user_preferences"
        const val LANGUAGE_KEY = "selected_language"
        const val FRENCH_CODE = "fr"
        const val CHECK_MARK = "\u2713"
    }
}