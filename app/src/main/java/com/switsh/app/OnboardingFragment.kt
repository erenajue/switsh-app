package com.switsh.app

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial

class OnboardingFragment : Fragment() {
    private companion object {
        const val REGISTRATION_TAG = "registration"
    }

    private val teal = Color.rgb(11, 110, 92)
    private val pageTitles = listOf(
        "Inscription",
        "Confirmation du numéro",
        "Authentification & sécurité",
        "Sélection du profil client",
        "Pièce d'identité",
        "Informations personnelles",
        "Justificatif de domicile",
        "Entreprise / PME (KYB)",
        "Statut de vérification",
    )

    private var page = 0
    private var businessProfile = false
    private lateinit var pageCount: TextView
    private lateinit var progress: LinearProgressIndicator
    private lateinit var screenContent: LinearLayout
    private lateinit var backButton: MaterialButton
    private lateinit var nextButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val context = requireContext()
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(244, 247, 246))
        }

        val top = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(12))
            setBackgroundColor(Color.WHITE)
        }
        val brandRow = LinearLayout(context).apply {
            gravity = Gravity.CENTER_VERTICAL
            orientation = LinearLayout.HORIZONTAL
        }
        brandRow.addView(text("S", 22, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            background = rounded(teal, 14)
        }, LinearLayout.LayoutParams(dp(42), dp(42)))
        val brand = text("Switsh", 19, Color.rgb(14, 29, 24), true).apply {
            setPadding(dp(12), 0, 0, 0)
        }
        brandRow.addView(brand)
        pageCount = text("", 12, Color.rgb(86, 104, 98), true).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        brandRow.addView(pageCount, LinearLayout.LayoutParams(0, dp(42), 1f))
        top.addView(brandRow)
        progress = LinearProgressIndicator(context).apply {
            max = pageTitles.size
            setProgressCompat(1, false)
            trackColor = Color.rgb(225, 235, 230)
            setIndicatorColor(teal)
        }
        top.addView(progress, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(5),
        ).apply { topMargin = dp(14) })
        root.addView(top)

        val scroll = ScrollView(context).apply {
            isFillViewport = false
            clipToPadding = false
            setPadding(dp(20), dp(18), dp(20), dp(16))
        }
        screenContent = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(screenContent)
        root.addView(scroll, LinearLayout.LayoutParams(0, 0, 1f))

        val footer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(20), dp(12), dp(20), dp(16))
            setBackgroundColor(Color.WHITE)
        }
        backButton = button("Retour", secondary = true)
        nextButton = button("Continuer")
        footer.addView(backButton, LinearLayout.LayoutParams(0, dp(54), 1f).apply {
            marginEnd = dp(8)
        })
        footer.addView(nextButton, LinearLayout.LayoutParams(0, dp(54), 1f).apply {
            marginStart = dp(8)
        })
        root.addView(footer)

        backButton.setOnClickListener {
            if (page > 0) showPage(page - 1)
        }
        nextButton.setOnClickListener { advance() }
        showPage(page)
        return root
    }

    private fun showPage(nextPage: Int) {
        page = nextPage.coerceIn(pageTitles.indices)
        pageCount.text = "${page + 1} / ${pageTitles.size}"
        progress.setProgressCompat(page + 1, true)
        backButton.isEnabled = page > 0
        backButton.alpha = if (page == 0) 0.45f else 1f
        nextButton.text = if (page == pageTitles.lastIndex) "Accéder à mon compte" else {
            when (page) {
                0 -> "Recevoir le code SMS"
                1 -> "Vérifier"
                2 -> "Activer la sécurité"
                7 -> "Soumettre le dossier KYB"
                else -> "Continuer"
            }
        }
        childFragmentManager.findFragmentByTag(REGISTRATION_TAG)?.let {
            childFragmentManager.beginTransaction().remove(it).commit()
        }
        screenContent.removeAllViews()
        when (page) {
            0 -> registrationScreen()
            1 -> otpScreen()
            2 -> securityScreen()
            3 -> profileScreen()
            4 -> identityScreen()
            5 -> personalInfoScreen()
            6 -> addressScreen()
            7 -> businessScreen()
            else -> verificationStatusScreen()
        }
    }

    private fun advance() {
        if (page == pageTitles.lastIndex) {
            Toast.makeText(requireContext(), "Votre compte est prêt dans ce prototype.", Toast.LENGTH_SHORT).show()
            return
        }
        if (page == 0) {
            val registration = childFragmentManager.findFragmentByTag(REGISTRATION_TAG) as? RegistrationFragment
            if (registration?.validate() == false) return
        }
        val nextPage = when (page) {
            3 -> if (businessProfile) 7 else 4
            6, 7 -> 8
            else -> page + 1
        }
        showPage(nextPage)
    }

    private fun registrationScreen() {
        val container = FrameLayout(requireContext()).apply { id = R.id.registrationContainer }
        screenContent.addView(container, itemMargins())
        childFragmentManager.beginTransaction()
            .replace(R.id.registrationContainer, RegistrationFragment(), REGISTRATION_TAG)
            .commit()
    }

    private fun otpScreen() {
        intro("Entrez le code reçu", "Un code à 6 chiffres a été envoyé au +243 9XX XXX XXX")
        field("Code OTP", "•  •  •  •  •  •", InputType.TYPE_CLASS_NUMBER)
        text("Le code expire dans 04:12 · 3 tentatives maximum", 12, Color.rgb(86, 104, 98))
            .also { screenContent.addView(it, itemMargins(top = 2, bottom = 14)) }
        noteCard("Ce code confirme votre numéro de ligne RDC conformément aux exigences ARPTC.", Color.rgb(232, 242, 255), Color.rgb(31, 111, 203))
        text("Renvoyer le code", 14, teal, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(16), 0, dp(8))
            setOnClickListener {
                Toast.makeText(requireContext(), "Un nouveau code vous sera envoyé.", Toast.LENGTH_SHORT).show()
            }
        }.also { screenContent.addView(it) }
    }

    private fun securityScreen() {
        intro("Sécurisez votre compte", "Définissez un code PIN et activez la biométrie")
        field("Créer un code PIN", "4 ou 6 chiffres", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        field("Confirmer le code PIN", "Saisir à nouveau le code", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        val biometrics = columnCard()
        biometrics.addView(switchRow("Face ID", "Utiliser Face ID pour déverrouiller", true))
        biometrics.addView(divider())
        biometrics.addView(switchRow("Empreinte digitale", "Utiliser l'empreinte digitale", false))
        screenContent.addView(biometrics, itemMargins(top = 8, bottom = 12))
        noteCard("Biométrie détectée automatiquement : Face ID disponible sur cet appareil.", Color.rgb(218, 238, 223), Color.rgb(46, 139, 87))
    }

    private fun profileScreen() {
        intro("Quel type de compte souhaitez-vous ouvrir ?", "Ce choix oriente votre parcours de vérification d'identité")
        profileChoice("Particulier", "Compte personnel bidevises USD / CDF", false)
        profileChoice("Entreprise / PME", "Compte société — dossier KYB requis", true)
        spinnerField("Pays de résidence fiscale", listOf("République démocratique du Congo", "Autre pays"))
    }

    private fun profileChoice(title: String, description: String, isBusiness: Boolean) {
        val selected = businessProfile == isBusiness
        val choice = columnCard(
            fill = if (selected) Color.rgb(234, 245, 242) else Color.WHITE,
            stroke = if (selected) teal else Color.rgb(220, 230, 225),
        )
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(text(if (isBusiness) "▦" else "◉", 23, teal, true).apply {
            gravity = Gravity.CENTER
            background = rounded(Color.rgb(214, 236, 228), 12)
        }, LinearLayout.LayoutParams(dp(44), dp(44)))
        val labels = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        labels.addView(text(title, 15, Color.rgb(14, 29, 24), true))
        labels.addView(text(description, 12, Color.rgb(86, 104, 98)).apply {
            setPadding(0, dp(3), 0, 0)
        })
        row.addView(labels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(text(if (selected) "●" else "○", 18, teal, true))
        choice.addView(row)
        choice.setOnClickListener {
            businessProfile = isBusiness
            showPage(page)
        }
        screenContent.addView(choice, itemMargins(bottom = 10))
    }

    private fun identityScreen() {
        intro("Vérifiez votre identité", "Document officiel RDC admis")
        spinnerField("Type de document", listOf("CENI", "ONIP / NIN", "Passeport biométrique", "Carte de résident"))
        field("Numéro du document", "Ex. 12-3456789-A00812")
        horizontalFields(
            "Date d'émission" to "JJ / MM / AAAA",
            "Date d'expiration" to "JJ / MM / AAAA",
        )
        sectionTitle("Documents & selfie")
        uploadCard("Photo recto — capturée", "Netteté vérifiée par l'OCR", true)
        uploadCard("Photographier le verso", "Requis pour les documents à double face")
        uploadCard("Selfie de vivacité", "Lancement du contrôle de vivacité")
        noteCard("Lecture automatique du document et contrôle de vivacité avant validation.", Color.rgb(232, 242, 255), Color.rgb(31, 111, 203))
    }

    private fun personalInfoScreen() {
        intro("Vos informations", "Pré-remplies depuis votre pièce d'identité")
        horizontalFields("Nom" to "KABUYA", "Prénom(s)" to "Mireille")
        horizontalFields("Date de naissance" to "JJ / MM / AAAA", "Sexe" to "Masculin / Féminin")
        field("Lieu de naissance", "Kinshasa, RDC")
        spinnerField("Nationalité", listOf("Congolaise (RDC)", "Autre"))
        field("Adresse complète", "Avenue de la Paix, n°12")
        field("Ville / Commune / Quartier", "Kinshasa / Gombe / Croisée")
        field("Profession", "Commerçante")
        spinnerField("Source des revenus déclarée", listOf("Salaire", "Activité commerciale", "Épargne", "Autre"))
        field("Revenu mensuel estimé (USD)", "0", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        noteCard("Le nom et prénom doivent correspondre au document scanné. Âge minimum : 18 ans.", Color.rgb(234, 245, 242), teal)
    }

    private fun addressScreen() {
        intro("Justificatif de domicile", "Nécessaire pour débloquer le Niveau 3 (plafonds élevés)")
        spinnerField("Type de justificatif", listOf(
            "Attestation de résidence",
            "Facture REGIDESO / SNEL",
            "Géolocalisation certifiée",
            "Déclaration sur l'honneur",
        ))
        field("Date d'émission", "JJ / MM / AAAA")
        uploadCard("Importer une photo ou un PDF", "Document scanné (sauf géolocalisation certifiée)")
        val location = columnCard()
        location.addView(switchRow("Géolocalisation certifiée", "Alternative si aucun document disponible", false))
        screenContent.addView(location, itemMargins(top = 8, bottom = 10))
    }

    private fun businessScreen() {
        intro("Dossier entreprise", "Informations légales de votre société")
        field("Raison sociale", "Switsh Trading SARL")
        horizontalFields("Numéro RCCM" to "RCCM", "NIF" to "NIF")
        spinnerField("Forme juridique", listOf("SARL", "SA", "Entreprise individuelle", "Autre"))
        field("Date de création", "JJ / MM / AAAA")
        spinnerField("Secteur d'activité", listOf("Commerce général", "Import-export", "Services", "Agroalimentaire", "Autre"))
        field("Adresse du siège social", "Adresse complète")
        field("Chiffre d'affaires annuel déclaré", "Montant en USD", InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        sectionTitle("Représentant légal & bénéficiaires effectifs")
        field("Nom et fonction du représentant", "Nom complet, fonction")
        uploadCard("Pièce d'identité du représentant", "CENI, passeport ou carte de résident")
        val beneficiary = columnCard(fill = Color.rgb(251, 252, 251))
        beneficiary.addView(text("MK   Mireille Kabuya", 14, Color.rgb(14, 29, 24), true))
        beneficiary.addView(text("Bénéficiaire effectif · 60% · Vérifié", 12, Color.rgb(86, 104, 98)).apply {
            setPadding(0, dp(5), 0, 0)
        })
        screenContent.addView(beneficiary, itemMargins(top = 6, bottom = 10))
        val addBeneficiary = button("+  Ajouter un bénéficiaire effectif", secondary = true)
        addBeneficiary.setOnClickListener {
            Toast.makeText(requireContext(), "Ajout d'un bénéficiaire effectif", Toast.LENGTH_SHORT).show()
        }
        screenContent.addView(addBeneficiary, itemMargins(bottom = 12))
        noteCard("La somme des pourcentages de détention déclarés doit être inférieure ou égale à 100%.", Color.rgb(247, 229, 196), Color.rgb(185, 119, 14))
    }

    private fun verificationStatusScreen() {
        intro("Identité vérifiée", "Votre compte est au Niveau 2 — plafonds intermédiaires actifs")
        val status = columnCard(fill = Color.WHITE)
        status.addView(statusRow("Statut KYC", "Décision finale", "VERIFIED", Color.rgb(46, 139, 87)))
        status.addView(divider())
        status.addView(statusRow("Niveau attribué", "Plafonds & fonctionnalités", "Niveau 2", Color.rgb(31, 111, 203)))
        status.addView(divider())
        status.addView(statusRow("Screening AML / PPE", "Listes CENAREF / ONU / GAFI", "CLEAR", Color.rgb(46, 139, 87)))
        screenContent.addView(status, itemMargins(bottom = 14))
        noteCard("Fournissez un justificatif de domicile pour atteindre le Niveau 3 et lever vos plafonds de transaction.", Color.rgb(247, 229, 196), Color.rgb(185, 119, 14))
        text("Votre identité et vos documents ont été vérifiés conformément aux contrôles KYC/AML.", 12, Color.rgb(86, 104, 98))
            .also { screenContent.addView(it, itemMargins(top = 16, bottom = 8)) }
    }

    private fun statusRow(title: String, detail: String, badge: String, badgeColor: Int): View {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(2), dp(11), dp(2), dp(11))
        }
        val labels = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        labels.addView(text(title, 14, Color.rgb(14, 29, 24), true))
        labels.addView(text(detail, 11, Color.rgb(86, 104, 98)).apply { setPadding(0, dp(3), 0, 0) })
        row.addView(labels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(text(badge, 11, badgeColor, true).apply {
            setPadding(dp(9), dp(5), dp(9), dp(5))
            background = rounded(if (badgeColor == teal) Color.rgb(214, 236, 228) else Color.rgb(218, 238, 223), 30)
        })
        return row
    }

    private fun intro(title: String, subtitle: String) {
        screenContent.addView(text(pageTitles[page].uppercase(), 10, teal, true), itemMargins(bottom = 8))
        screenContent.addView(text(title, 24, Color.rgb(14, 29, 24), true), itemMargins(bottom = 6))
        screenContent.addView(text(subtitle, 14, Color.rgb(86, 104, 98)), itemMargins(bottom = 18))
    }

    private fun field(
        label: String,
        hint: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        prefix: String? = null,
    ) {
        screenContent.addView(text(label, 13, Color.rgb(14, 29, 24), true), itemMargins(bottom = 6))
        val entry = EditText(requireContext()).apply {
            this.hint = hint
            this.inputType = inputType
            textSize = 14f
            setSingleLine(true)
            setTextColor(Color.rgb(14, 29, 24))
            setHintTextColor(Color.rgb(147, 163, 157))
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(Color.WHITE, 12, Color.rgb(220, 230, 225))
        }
        if (prefix != null) {
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            row.addView(text(prefix, 12, teal, true).apply { gravity = Gravity.CENTER }, LinearLayout.LayoutParams(dp(68), dp(54)))
            row.addView(entry, LinearLayout.LayoutParams(0, dp(54), 1f))
            row.background = rounded(Color.WHITE, 12, Color.rgb(220, 230, 225))
            screenContent.addView(row, itemMargins(bottom = 12))
        } else {
            screenContent.addView(entry, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54),
            ).apply { bottomMargin = dp(12) })
        }
    }

    private fun spinnerField(label: String, options: List<String>) {
        screenContent.addView(text(label, 13, Color.rgb(14, 29, 24), true), itemMargins(bottom = 6))
        val spinner = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)
            background = rounded(Color.WHITE, 12, Color.rgb(220, 230, 225))
            setPadding(dp(10), 0, dp(10), 0)
        }
        screenContent.addView(spinner, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(54),
        ).apply { bottomMargin = dp(12) })
    }

    private fun horizontalFields(vararg fields: Pair<String, String>) {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        fields.forEachIndexed { index, (label, hint) ->
            val item = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
            item.addView(text(label, 12, Color.rgb(14, 29, 24), true), itemMargins(bottom = 5))
            item.addView(EditText(requireContext()).apply {
                this.hint = hint
                textSize = 12f
                setSingleLine(true)
                setPadding(dp(9), 0, dp(9), 0)
                setTextColor(Color.rgb(14, 29, 24))
                setHintTextColor(Color.rgb(147, 163, 157))
                background = rounded(Color.WHITE, 11, Color.rgb(220, 230, 225))
            }, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52)))
            row.addView(item, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                if (index > 0) marginStart = dp(8)
                if (index == 0) marginEnd = dp(4)
            })
        }
        screenContent.addView(row, itemMargins(bottom = 12))
    }

    private fun uploadCard(title: String, detail: String, complete: Boolean = false) {
        val card = columnCard(fill = if (complete) Color.rgb(234, 245, 242) else Color.WHITE)
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(text(if (complete) "✓" else "＋", 20, teal, true).apply { gravity = Gravity.CENTER }, LinearLayout.LayoutParams(dp(38), dp(38)))
        val labels = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }
        labels.addView(text(title, 13, Color.rgb(14, 29, 24), true))
        labels.addView(text(detail, 11, Color.rgb(86, 104, 98)).apply { setPadding(0, dp(3), 0, 0) })
        row.addView(labels)
        card.addView(row)
        card.setOnClickListener {
            if (!complete) Toast.makeText(requireContext(), "Ouverture de l'appareil photo / des fichiers", Toast.LENGTH_SHORT).show()
        }
        screenContent.addView(card, itemMargins(bottom = 9))
    }

    private fun switchRow(title: String, detail: String, checked: Boolean): View {
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, dp(8))
        }
        val labels = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL }
        labels.addView(text(title, 14, Color.rgb(14, 29, 24), true))
        labels.addView(text(detail, 11, Color.rgb(86, 104, 98)).apply { setPadding(0, dp(3), 0, 0) })
        row.addView(labels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(SwitchMaterial(requireContext()).apply {
            isChecked = checked
            thumbTintList = android.content.res.ColorStateList.valueOf(teal)
        })
        return row
    }

    private fun noteCard(message: String, fill: Int, accent: Int) {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(13), dp(13), dp(13), dp(13))
            background = rounded(fill, 13)
        }
        card.addView(text("●", 12, accent, true).apply {
            setPadding(0, 0, dp(9), 0)
            gravity = Gravity.TOP
        })
        card.addView(text(message, 12, Color.rgb(55, 76, 68)))
        screenContent.addView(card, itemMargins(bottom = 12))
    }

    private fun sectionTitle(title: String) {
        screenContent.addView(text(title, 14, Color.rgb(14, 29, 24), true), itemMargins(top = 6, bottom = 10))
    }

    private fun columnCard(
        fill: Int = Color.WHITE,
        stroke: Int = Color.rgb(220, 230, 225),
    ): LinearLayout = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(14), dp(12), dp(14), dp(12))
        background = rounded(fill, 14, stroke)
    }

    private fun divider(): View = View(requireContext()).apply {
        setBackgroundColor(Color.rgb(230, 238, 234))
    }.also {
        it.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1))
    }

    private fun button(label: String, secondary: Boolean = false): MaterialButton =
        MaterialButton(requireContext()).apply {
            text = label
            isAllCaps = false
            textSize = 14f
            cornerRadius = dp(14)
            if (secondary) {
                setTextColor(teal)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.rgb(234, 245, 242))
                strokeColor = android.content.res.ColorStateList.valueOf(Color.rgb(214, 236, 228))
                strokeWidth = dp(1)
            } else {
                setTextColor(Color.WHITE)
                backgroundTintList = android.content.res.ColorStateList.valueOf(teal)
            }
        }

    private fun text(value: String, size: Int, color: Int, bold: Boolean = false): TextView =
        TextView(requireContext()).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
        }

    private fun rounded(fill: Int, radius: Int, stroke: Int? = null): GradientDrawable =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()
            if (stroke != null) setStroke(dp(1), stroke)
        }

    private fun itemMargins(top: Int = 0, bottom: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(top)
            bottomMargin = dp(bottom)
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
