package com.switsh.app

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

/** « Inscription » form: phone (+243), e-mail, password and CGU/CGV consent. */
class RegistrationFragment : Fragment() {
    private val teal = Color.rgb(11, 110, 92)
    private val textColor = Color.rgb(14, 29, 24)
    private val mutedColor = Color.rgb(86, 104, 98)
    private val errorColor = Color.rgb(194, 59, 59)
    private val borderColor = Color.rgb(220, 230, 225)

    private lateinit var phoneInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var consentBox: CheckBox
    private lateinit var phoneRow: View
    private lateinit var phoneError: TextView
    private lateinit var emailError: TextView
    private lateinit var passwordError: TextView
    private lateinit var consentError: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val form = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }

        form.addView(label("Créer votre compte", 24, bold = true, color = textColor), margins(bottom = 6))
        form.addView(label("100% mobile — vérifié par SMS et e-mail", 14, color = mutedColor), margins(bottom = 20))

        form.addView(fieldLabel("Numéro de téléphone"), margins(bottom = 6))
        phoneInput = input("9XX XXX XXX", InputType.TYPE_CLASS_PHONE).apply { background = null }
        phoneRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = outline(borderColor)
            addView(label("🇨🇩 +243", 13, bold = true, color = teal).apply {
                gravity = Gravity.CENTER
                setPadding(dp(12), 0, dp(12), 0)
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(54)))
            addView(View(context).apply { setBackgroundColor(borderColor) }, LinearLayout.LayoutParams(dp(1), dp(26)))
            addView(phoneInput, LinearLayout.LayoutParams(0, dp(54), 1f))
        }
        form.addView(phoneRow, margins(bottom = 4))
        phoneError = hint("Format RDC +243 suivi de 9 chiffres")
        form.addView(phoneError, margins(bottom = 14))

        form.addView(fieldLabel("Adresse e-mail"), margins(bottom = 6))
        emailInput = input("nom@exemple.cd", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        form.addView(emailInput, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54)).apply {
            bottomMargin = dp(4)
        })
        emailError = hint("Format valide requis, adresse unique")
        form.addView(emailError, margins(bottom = 14))

        form.addView(fieldLabel("Mot de passe"), margins(bottom = 6))
        passwordInput = input("••••••••", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        form.addView(passwordInput, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54)).apply {
            bottomMargin = dp(4)
        })
        passwordError = hint("Min. 8 caractères, majuscule, chiffre, caractère spécial")
        form.addView(passwordError, margins(bottom = 14))

        consentBox = CheckBox(requireContext()).apply {
            buttonTintList = ColorStateList.valueOf(teal)
            text = consentText()
            textSize = 13f
            setTextColor(mutedColor)
            movementMethod = LinkMovementMethod.getInstance()
        }
        form.addView(consentBox, margins(top = 4))
        consentError = hint("").apply { visibility = View.GONE }
        form.addView(consentError, margins(bottom = 8))

        return form
    }

    /** Validates every field and shows inline errors; returns true when the form can be submitted. */
    fun validate(): Boolean {
        var valid = true

        val phoneDigits = phoneInput.text.toString().filter { it.isDigit() }
        valid = flag(phoneError, phoneRow, phoneDigits.length == 9,
            "Saisissez 9 chiffres après +243", "Format RDC +243 suivi de 9 chiffres") && valid

        val emailValue = emailInput.text.toString().trim()
        valid = flag(emailError, emailInput, Patterns.EMAIL_ADDRESS.matcher(emailValue).matches(),
            "Adresse e-mail invalide", "Format valide requis, adresse unique") && valid

        valid = flag(passwordError, passwordInput, isStrongPassword(passwordInput.text.toString()),
            "8 caractères min., avec majuscule, chiffre et caractère spécial",
            "Min. 8 caractères, majuscule, chiffre, caractère spécial") && valid

        consentError.visibility = if (consentBox.isChecked) View.GONE else View.VISIBLE
        if (!consentBox.isChecked) {
            consentError.text = "Vous devez accepter les conditions pour poursuivre"
            consentError.setTextColor(errorColor)
            valid = false
        }
        return valid
    }

    fun phoneNumber(): String = "+243" + phoneInput.text.toString().filter { it.isDigit() }

    fun email(): String = emailInput.text.toString().trim()

    private fun isStrongPassword(value: String): Boolean =
        value.length >= 8 && value.any { it.isUpperCase() } && value.any { it.isDigit() } &&
            value.any { !it.isLetterOrDigit() }

    private fun flag(messageView: TextView, field: View, ok: Boolean, error: String, normal: String): Boolean {
        messageView.text = if (ok) normal else error
        messageView.setTextColor(if (ok) mutedColor else errorColor)
        field.background = outline(if (ok) borderColor else errorColor)
        return ok
    }

    private fun consentText(): SpannableString {
        val full = "J'accepte les Conditions générales et la Politique de confidentialité Switsh."
        val spannable = SpannableString(full)
        listOf("Conditions générales", "Politique de confidentialité").forEach { link ->
            val start = full.indexOf(link)
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Toast.makeText(requireContext(), link, Toast.LENGTH_SHORT).show()
                }

                override fun updateDrawState(ds: android.text.TextPaint) {
                    ds.color = teal
                    ds.isUnderlineText = false
                }
            }, start, start + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(StyleSpan(Typeface.BOLD), start, start + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(teal), start, start + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    private fun fieldLabel(value: String): TextView {
        val spannable = SpannableString("$value *")
        spannable.setSpan(ForegroundColorSpan(errorColor), value.length + 1, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return label("", 13, bold = true, color = textColor).apply { text = spannable }
    }

    private fun hint(value: String): TextView = label(value, 11, color = mutedColor)

    private fun input(hintText: String, type: Int): EditText = EditText(requireContext()).apply {
        hint = hintText
        inputType = type
        textSize = 14f
        setSingleLine(true)
        setTextColor(textColor)
        setHintTextColor(Color.rgb(147, 163, 157))
        setPadding(dp(14), 0, dp(14), 0)
        background = outline(borderColor)
    }

    private fun label(value: String, size: Int, bold: Boolean = false, color: Int): TextView =
        TextView(requireContext()).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
        }

    private fun outline(stroke: Int) = GradientDrawable().apply {
        setColor(Color.WHITE)
        cornerRadius = dp(12).toFloat()
        setStroke(dp(1), stroke)
    }

    private fun margins(top: Int = 0, bottom: Int = 0) =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(top)
            bottomMargin = dp(bottom)
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
