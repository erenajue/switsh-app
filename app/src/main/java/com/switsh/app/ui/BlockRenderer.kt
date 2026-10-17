package com.switsh.app.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.widget.ImageViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.switsh.app.R
import com.switsh.app.model.Block
import com.switsh.app.model.Item
import com.switsh.app.model.Screen
import com.switsh.app.model.ScreenLabels
import java.util.Calendar
import java.util.Locale

/** Per-screen interaction state (selected choice, selected language). */
class ScreenState {
    var choice = 0
    var languageCode: String? = null
    val inputValues = mutableMapOf<String, String>()
    var termsAccepted = true
}

class RenderedScreen(val content: LinearLayout, val cta: View?, val state: ScreenState)

interface RenderActions {
    fun navigate(id: String)
    fun primary(button: Block, state: ScreenState, onComplete: (Boolean) -> Unit = {})
    fun action(action: String)
    fun message(text: String)
    fun pinEntered()
    fun currentLanguage(): String
}

/** Builds native views for the block-based screen definitions generated from the prototype. */
class BlockRenderer(private val ctx: Context, private val actions: RenderActions) {
    private val ui = UiKit(ctx)
    private lateinit var state: ScreenState
    private val choiceRows = mutableListOf<Pair<View, String>>()
    private val languageRows = mutableListOf<Pair<View, String>>()
    private val choiceViews = mutableListOf<LinearLayout>()

    fun render(screen: Screen): RenderedScreen {
        state = ScreenState().apply { languageCode = actions.currentLanguage() }
        choiceViews.clear()
        val content = ui.vertical()
        var cta: View? = null
        for (block in screen.blocks) {
            if (block.t == "cta") cta = buildCta(block) else addBlock(content, block)
        }
        return RenderedScreen(content, cta, state)
    }

    private fun addBlock(parent: LinearLayout, b: Block) {
        when (b.t) {
            "title" -> parent.addView(ui.text(b.text, 22f, bold = true).apply { setLineSpacing(0f, 1.05f) }, ui.lp(top = 4, bottom = 4))
            "subtitle" -> parent.addView(ui.text(b.text, 14f, R.color.text_muted), ui.lp(bottom = 16))
            "section" -> parent.addView(
                ui.text(b.text?.uppercase(Locale.ROOT), 11f, R.color.text_faint, bold = true).apply { letterSpacing = 0.08f },
                ui.lp(top = 12, bottom = 8),
            )
            "text" -> parent.addView(ui.text(b.text, 12f, R.color.text_muted), ui.lp(bottom = 12))
            "hero" -> parent.addView(hero(b), ui.lp(top = 8, bottom = 20))
            "field" -> parent.addView(field(b), ui.lp(bottom = 14))
            "fieldrow" -> parent.addView(fieldRow(b), ui.lp())
            "check" -> parent.addView(check(b), ui.lp(bottom = 12))
            "notice" -> parent.addView(notice(b), ui.lp(bottom = 14))
            "card" -> addCard(parent, b)
            "row" -> parent.addView(row(b, standalone = true), ui.lp(bottom = 8))
            "bankcard" -> parent.addView(bankCard(b), ui.lp(bottom = 14))
            "choice" -> parent.addView(choice(b), ui.lp(bottom = 10))
            "button" -> parent.addView(button(b), ui.lp(bottom = 8))
            "balance" -> parent.addView(balance(b), ui.lp(bottom = 14))
            "wallets" -> parent.addView(wallets(b), ui.lp(bottom = 14))
            "quick" -> parent.addView(quick(b), ui.lp(bottom = 18))
            "segmented" -> parent.addView(segmented(b), ui.lp(bottom = 14))
            "pills" -> parent.addView(pills(b), ui.lp(bottom = 14))
            "divider" -> parent.addView(View(ctx).apply { setBackgroundColor(ui.color(R.color.divider)) }, ui.lp(height = ui.dp(1), top = 8, bottom = 12))
            "progress" -> parent.addView(progress(b), ui.lp(top = 4, bottom = 14))
            "chat" -> parent.addView(chat(b), chatParams(b))
            "greeting" -> parent.addView(greeting(b), ui.lp(bottom = 16))
            "sectionlink" -> parent.addView(sectionLink(b), ui.lp(top = 4, bottom = 8))
            "icon" -> parent.addView(ui.icon(b.icon, 40, R.color.primary), ui.lp(width = ui.dp(40), height = ui.dp(40), bottom = 12))
            "languages" -> parent.addView(languages(b), ui.lp(bottom = 8))
            "pinpad" -> parent.addView(pinPad(b), ui.lp())
        }
    }

    private fun hero(b: Block): View = ui.vertical().apply {
        gravity = Gravity.CENTER_HORIZONTAL
        addView(
            FrameLayout(ctx).apply {
                background = ui.rounded(R.color.primary_highlight, 24)
                addView(ui.icon(b.icon, 32, R.color.primary), FrameLayout.LayoutParams(ui.dp(32), ui.dp(32), Gravity.CENTER))
            },
            ui.lp(width = ui.dp(72), height = ui.dp(72), bottom = 14),
        )
        addView(ui.text(b.title, 22f, bold = true).apply { gravity = Gravity.CENTER }, ui.lp(bottom = 6))
        addView(ui.text(b.sub, 14f, R.color.text_muted).apply { gravity = Gravity.CENTER }, ui.lp())
    }

    private fun fieldRow(b: Block): View = ui.horizontal(centered = false).apply {
        weightSum = (b.children?.size ?: 1).toFloat()
        b.children?.forEachIndexed { index, child ->
            addView(field(child), ui.lp(width = 0, weight = 1f, bottom = 14, start = if (index == 0) 0 else 6, end = if (index == b.children.lastIndex) 0 else 6))
        }
    }

    private fun field(b: Block): View {
        if (b.ui == "toggle") return toggleField(b)
        return ui.vertical().apply {
            if (!b.label.isNullOrBlank()) addView(fieldLabel(b), ui.lp(bottom = 6))
            addView(
                when (b.ui) {
                    "select" -> selectBox(b)
                    "otp" -> otpBoxes(b)
                    "file" -> fileBox(b)
                    "readonly" -> readOnlyBox(b)
                    "textarea" -> inputBox(b, multiline = true)
                    else -> inputBox(b)
                },
                ui.lp(),
            )
            if (!b.hint.isNullOrBlank()) addView(ui.text(b.hint, 11f, R.color.text_faint), ui.lp(top = 4))
        }
    }

    private fun fieldLabel(b: Block): View = ui.horizontal().apply {
        addView(ui.text(b.label, 13f, R.color.text, bold = true))
        when (b.required) {
            "yes" -> addView(ui.text(" *", 13f, R.color.error, bold = true))
            "cond" -> addView(ui.text(" ${ScreenLabels.resolve("{{common.applicable}}", actions.currentLanguage())}", 11f, R.color.text_faint))
        }
    }

    private fun toggleField(b: Block): View = ui.horizontal().apply {
        background = ui.rounded(R.color.surface, 11, R.color.border)
        setPadding(ui.dp(14), ui.dp(10), ui.dp(14), ui.dp(10))
        val labels = ui.vertical()
        labels.addView(ui.text(b.label ?: b.placeholder, 14f, bold = true))
        if (!b.hint.isNullOrBlank()) labels.addView(ui.text(b.hint, 12f, R.color.text_muted))
        addView(labels, ui.lp(width = 0, weight = 1f))
        addView(MaterialSwitch(ctx).apply { isChecked = b.on })
    }

    private fun inputBox(b: Block, multiline: Boolean = false): View {
        val box = ui.horizontal().apply {
            background = ui.rounded(R.color.surface, 11, R.color.border, 1.5f)
            setPadding(ui.dp(14), 0, ui.dp(14), 0)
        }
        if (!b.prefix.isNullOrBlank()) box.addView(ui.text(b.prefix, 14f, R.color.text_muted, bold = true), ui.lp(width = ViewGroup.LayoutParams.WRAP_CONTENT, end = 8))
        val edit = EditText(ctx).apply {
            background = null
            textSize = 14f
            hint = b.placeholder
            setTextColor(ui.color(R.color.text))
            setHintTextColor(ui.color(R.color.text_faint))
            setPadding(0, ui.dp(13), 0, ui.dp(13))
            inputType = when {
                multiline -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                b.ui == "email" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                b.ui == "password" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                b.ui == "number" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                b.prefix?.startsWith("+") == true -> InputType.TYPE_CLASS_PHONE
                else -> InputType.TYPE_CLASS_TEXT
            }
            b.fieldId?.let { fieldId ->
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                    override fun afterTextChanged(s: Editable?) {
                        state.inputValues[fieldId] = s?.toString().orEmpty()
                    }
                })
                state.inputValues[fieldId] = text.toString()
            }
            if (multiline) {
                minLines = 3
                gravity = Gravity.TOP
            }
        }
        if (b.ui == "date") {
            edit.isFocusable = false
            edit.isClickable = true
            val open = View.OnClickListener { showDatePicker(edit) }
            edit.setOnClickListener(open)
            box.setOnClickListener(open)
        }
        edit.setOnFocusChangeListener { _, focused ->
            box.background = if (focused) ui.rounded(R.color.surface, 11, R.color.primary, 1.5f) else ui.rounded(R.color.surface, 11, R.color.border, 1.5f)
        }
        box.addView(edit, ui.lp(width = 0, weight = 1f))
        if (!b.icon.isNullOrBlank()) {
            box.addView(ui.icon(b.icon, 20, R.color.primary).apply { setOnClickListener { actions.message(ctx.getString(R.string.example_only)) } }, ui.lp(width = ui.dp(20), height = ui.dp(20), start = 8))
        } else if (b.ui == "date") {
            box.addView(ui.icon("chevron_down", 18, R.color.text_faint), ui.lp(width = ui.dp(18), height = ui.dp(18), start = 8))
        }
        return box
    }

    private fun showDatePicker(target: EditText) {
        val now = Calendar.getInstance()
        DatePickerDialog(ctx, { _, y, m, d -> target.setText(String.format(Locale.ROOT, "%02d/%02d/%04d", d, m + 1, y)) },
            now.get(Calendar.YEAR) - 25, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun readOnlyBox(b: Block): View = ui.text(b.placeholder ?: b.text ?: "", 14f, R.color.text_muted).apply {
        background = ui.rounded(R.color.surface_offset, 11, R.color.border)
        setPadding(ui.dp(14), ui.dp(13), ui.dp(14), ui.dp(13))
    }

    private fun selectBox(b: Block): View {
        val options = b.options.orEmpty()
        val frame = FrameLayout(ctx).apply { background = ui.rounded(R.color.surface, 11, R.color.border, 1.5f) }
        val adapter = object : ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, options) {
            private fun style(v: View, dropdown: Boolean): View = (v as TextView).apply {
                textSize = 14f
                setTextColor(ui.color(R.color.text))
                if (dropdown) setPadding(ui.dp(16), ui.dp(14), ui.dp(16), ui.dp(14)) else setPadding(ui.dp(14), 0, ui.dp(36), 0)
            }
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = style(super.getView(position, convertView, parent), false)
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = style(super.getDropDownView(position, convertView, parent), true)
        }
        frame.addView(Spinner(ctx).apply {
            background = null
            this.adapter = adapter
        }, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ui.dp(48)))
        frame.addView(
            ui.icon("chevron_down", 18, R.color.text_faint).apply { isClickable = false },
            FrameLayout.LayoutParams(ui.dp(18), ui.dp(18), Gravity.END or Gravity.CENTER_VERTICAL).apply { marginEnd = ui.dp(14) },
        )
        return frame
    }

    private fun otpBoxes(b: Block): View {
        val count = if (b.length > 0) b.length else 6
        val row = ui.horizontal()
        val boxes = mutableListOf<EditText>()
        repeat(count) { index ->
            val edit = EditText(ctx).apply {
                background = ui.rounded(R.color.surface, 11, R.color.border, 1.5f)
                gravity = Gravity.CENTER
                textSize = 20f
                setTypeface(typeface, Typeface.BOLD)
                setTextColor(ui.color(R.color.text))
                inputType = InputType.TYPE_CLASS_NUMBER
                filters = arrayOf(android.text.InputFilter.LengthFilter(1))
                setPadding(0, 0, 0, 0)
            }
            edit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
                override fun onTextChanged(s: CharSequence?, st: Int, b4: Int, c: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty() && index < count - 1) boxes[index + 1].requestFocus()
                }
            })
            edit.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && edit.text.isEmpty() && index > 0) {
                    boxes[index - 1].apply { setText(""); requestFocus() }
                    true
                } else false
            }
            boxes += edit
            row.addView(edit, ui.lp(width = 0, height = ui.dp(54), weight = 1f, start = if (index == 0) 0 else 4, end = if (index == count - 1) 0 else 4))
        }
        return row
    }

    private fun fileBox(b: Block): View {
        var done = b.done
        val box = ui.vertical().apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(ui.dp(16), ui.dp(20), ui.dp(16), ui.dp(20))
        }
        val iconView = ui.icon(if (done) "check_circle" else (b.icon ?: "upload"), 26, R.color.primary)
        val uploadTitle = b.title ?: b.placeholder ?: ScreenLabels.resolve("{{common.add_file}}", actions.currentLanguage()).orEmpty()
        val uploadedTitle = ScreenLabels.resolve("{{common.file_added}}", actions.currentLanguage()).orEmpty()
        val titleView = ui.text(uploadTitle, 13f, bold = true).apply { gravity = Gravity.CENTER }
        val subView = ui.text(b.sub, 11f, R.color.text_faint).apply { gravity = Gravity.CENTER }
        fun refresh() {
            box.background = if (done) ui.rounded(R.color.success_highlight, 11, R.color.success, 1.5f)
            else ui.rounded(R.color.surface, 11, R.color.border, 1.5f, dashed = true)
            iconView.setImageResource(ui.iconRes(if (done) "check_circle" else (b.icon ?: "upload")))
            ImageViewCompat.setImageTintList(iconView, ColorStateList.valueOf(ui.color(if (done) R.color.success else R.color.primary)))
            titleView.text = if (done) uploadedTitle else uploadTitle
        }
        box.addView(iconView, ui.lp(width = ui.dp(26), height = ui.dp(26), bottom = 6))
        box.addView(titleView, ui.lp())
        if (!b.sub.isNullOrBlank()) box.addView(subView, ui.lp(top = 2))
        refresh()
        box.setOnClickListener { done = !done; refresh() }
        return box
    }

    private fun check(b: Block): View = MaterialCheckBox(ctx).apply {
        text = b.text
        textSize = 13f
        isChecked = b.checked
        b.fieldId?.let { fieldId ->
            state.termsAccepted = b.checked
            setOnCheckedChangeListener { _, checked ->
                state.termsAccepted = checked
                state.inputValues[fieldId] = checked.toString()
            }
        }
        setTextColor(ui.color(R.color.text_muted))
    }

    private fun notice(b: Block): View {
        val (bg, fg) = when (b.kind) {
            "warn" -> R.color.warning_highlight to R.color.warning
            "success" -> R.color.success_highlight to R.color.success
            else -> R.color.primary_highlight to R.color.primary_active
        }
        return ui.horizontal(centered = false).apply {
            background = ui.rounded(bg, 12)
            setPadding(ui.dp(14), ui.dp(12), ui.dp(14), ui.dp(12))
            val iconTint = if (b.kind == "warn") R.color.warning else if (b.kind == "success") R.color.success else R.color.primary
            addView(ui.icon(b.icon ?: "shield", 18, iconTint), ui.lp(width = ui.dp(18), height = ui.dp(18), top = 1, end = 10))
            val texts = ui.vertical()
            texts.addView(ui.text(b.text, 12f, fg).apply { setLineSpacing(0f, 1.1f) })
            if (!b.label.isNullOrBlank()) texts.addView(ui.text(b.label, 12f, fg, bold = true), ui.lp(top = 6))
            addView(texts, ui.lp(width = 0, weight = 1f))
        }
    }

    private fun addCard(parent: LinearLayout, b: Block) {
        val children = b.children.orEmpty()
        if (b.bare) {
            children.forEach { addBlock(parent, it) }
            return
        }
        val card = ui.vertical().apply {
            background = ui.rounded(R.color.surface, 17, R.color.border)
            setPadding(ui.dp(16), ui.dp(8), ui.dp(16), ui.dp(8))
        }
        children.forEachIndexed { index, child ->
            if (index > 0 && child.t == "row" && children[index - 1].t == "row") {
                card.addView(View(ctx).apply { setBackgroundColor(ui.color(R.color.divider)) }, ui.lp(height = ui.dp(1)))
            }
            when (child.t) {
                "row" -> card.addView(row(child, standalone = false), ui.lp())
                "field" -> card.addView(field(child), ui.lp(top = 8))
                "fieldrow" -> card.addView(fieldRow(child), ui.lp(top = 8))
                else -> addBlock(card, child)
            }
        }
        parent.addView(card, ui.lp(bottom = 14))
    }

    private fun row(b: Block, standalone: Boolean): View {
        val row = ui.horizontal().apply {
            setPadding(if (standalone) ui.dp(14) else 0, ui.dp(12), if (standalone) ui.dp(14) else 0, ui.dp(12))
            if (standalone) background = ui.rounded(R.color.surface, 14, R.color.border)
        }
        val tintBg = if (b.tint == "accent") R.color.accent_highlight else R.color.primary_highlight
        val tintFg = if (b.tint == "accent") R.color.accent_hover else R.color.primary
        if (!b.lead.isNullOrBlank()) {
            row.addView(
                ui.text(b.lead, 13f, R.color.primary, bold = true).apply {
                    gravity = Gravity.CENTER
                    background = ui.circle(R.color.primary_highlight)
                },
                ui.lp(width = ui.dp(40), height = ui.dp(40), end = 12),
            )
        } else if (!b.icon.isNullOrBlank()) {
            row.addView(
                FrameLayout(ctx).apply {
                    background = ui.rounded(tintBg, 12)
                    addView(ui.icon(b.icon, 20, tintFg), FrameLayout.LayoutParams(ui.dp(20), ui.dp(20), Gravity.CENTER))
                },
                ui.lp(width = ui.dp(40), height = ui.dp(40), end = 12),
            )
        }
        val texts = ui.vertical()
        texts.addView(ui.text(b.title, 14f, bold = true))
        if (!b.sub.isNullOrBlank()) texts.addView(ui.text(b.sub, 12f, R.color.text_muted))
        row.addView(texts, ui.lp(width = 0, weight = 1f))
        b.trailing?.let { row.addView(trailing(it), ui.lp(width = ViewGroup.LayoutParams.WRAP_CONTENT, start = 8)) }
        if (!b.to.isNullOrBlank()) {
            row.setOnClickListener { actions.navigate(b.to) }
            ui.ripple(row)
        }
        return row
    }

    private fun trailing(t: com.switsh.app.model.Trailing): View = when (t.kind) {
        "toggle" -> MaterialSwitch(ctx).apply { isChecked = t.on }
        "chevron" -> ui.icon("chevron_right", 18, R.color.text_faint).apply { layoutParams = LinearLayout.LayoutParams(ui.dp(18), ui.dp(18)) }
        "badge" -> badge(t.text, t.style)
        "amount" -> ui.text(t.text, 14f, if (t.style == "pos") R.color.success else R.color.text, bold = true)
        else -> ui.text(t.text, 13f, R.color.text_muted, bold = t.bold)
    }

    private fun badge(text: String?, style: String?): View {
        val (bg, fg) = when (style) {
            "badge-success" -> R.color.success_highlight to R.color.success
            "badge-usd" -> R.color.usd_highlight to R.color.usd
            "badge-warning" -> R.color.warning_highlight to R.color.warning
            "badge-error" -> R.color.error_highlight to R.color.error
            else -> R.color.primary_highlight to R.color.primary
        }
        return ui.text(text, 11f, fg, bold = true).apply {
            background = ui.rounded(bg, 20)
            setPadding(ui.dp(10), ui.dp(3), ui.dp(10), ui.dp(3))
        }
    }

    private fun bankCard(b: Block): View = ui.vertical().apply {
        background = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(ui.color(R.color.primary), ui.color(R.color.card_dark))).apply {
            cornerRadius = ui.dpf(17.6f)
        }
        setPadding(ui.dp(20), ui.dp(20), ui.dp(20), ui.dp(20))
        val top = ui.horizontal()
        top.addView(ui.text(b.label, 11f, R.color.white).apply { alpha = 0.8f; letterSpacing = 0.05f }, ui.lp(width = 0, weight = 1f))
        if (!b.badge.isNullOrBlank()) {
            top.addView(ui.text(b.badge, 11f, R.color.white, bold = true).apply {
                background = ui.rounded(null, 20).apply { setColor(0x2EFFFFFF) }
                setPadding(ui.dp(10), ui.dp(3), ui.dp(10), ui.dp(3))
            })
        } else if (!b.icon.isNullOrBlank()) {
            top.addView(ui.icon(b.icon, 20, R.color.white))
        }
        addView(top, ui.lp())
        addView(ui.text(b.number, 18f, R.color.white).apply { typeface = Typeface.MONOSPACE; letterSpacing = 0.1f }, ui.lp(top = 20, bottom = 12))
        val bottom = ui.horizontal()
        bottom.addView(ui.text(b.holder, 11f, R.color.white).apply { alpha = 0.85f }, ui.lp(width = 0, weight = 1f))
        bottom.addView(ui.text(b.extra, 11f, R.color.white).apply { alpha = 0.85f })
        addView(bottom, ui.lp())
    }

    private fun choice(b: Block): View {
        val box = ui.horizontal().apply {
            setPadding(ui.dp(16), ui.dp(14), ui.dp(16), ui.dp(14))
            ui.ripple(this)
        }
        val index = choiceViews.size
        box.addView(
            FrameLayout(ctx).apply {
                background = ui.rounded(R.color.primary_highlight, 12)
                addView(ui.icon(b.icon, 22, R.color.primary), FrameLayout.LayoutParams(ui.dp(22), ui.dp(22), Gravity.CENTER))
            },
            ui.lp(width = ui.dp(44), height = ui.dp(44), end = 14),
        )
        val texts = ui.vertical()
        texts.addView(ui.text(b.title, 15f, bold = true))
        texts.addView(ui.text(b.sub, 12f, R.color.text_muted))
        box.addView(texts, ui.lp(width = 0, weight = 1f))
        choiceViews += box
        if (b.selected) state.choice = index
        box.setOnClickListener {
            state.choice = index
            refreshChoices()
        }
        refreshChoices()
        return box
    }

    private fun refreshChoices() {
        choiceViews.forEachIndexed { i, v ->
            v.background = if (i == state.choice) ui.rounded(R.color.primary_highlight, 14, R.color.primary, 2f) else ui.rounded(R.color.surface, 14, R.color.border, 1.5f)
        }
    }

    private fun button(b: Block): View {
        val button = MaterialButton(ctx)
        val (bg, fg, stroke) = when (b.style) {
            "secondary" -> Triple(R.color.surface, R.color.primary, R.color.primary)
            "ghost" -> Triple(null, R.color.primary, null)
            "danger" -> Triple(R.color.error_highlight, R.color.error, null)
            else -> Triple(R.color.primary, R.color.white, null)
        }
        button.apply {
            text = b.text
            isAllCaps = false
            textSize = if (b.small) 13f else 15f
            setTypeface(typeface, Typeface.BOLD)
            cornerRadius = ui.dp(12)
            insetTop = 0
            insetBottom = 0
            minHeight = ui.dp(if (b.small) 40 else 52)
            backgroundTintList = ColorStateList.valueOf(if (bg != null) ui.color(bg) else android.graphics.Color.TRANSPARENT)
            setTextColor(ui.color(fg))
            if (stroke != null) {
                strokeColor = ColorStateList.valueOf(ui.color(stroke))
                strokeWidth = ui.dp(2)
            }
            if (!b.icon.isNullOrBlank()) {
                val res = ui.iconRes(b.icon)
                if (res != 0) {
                    setIconResource(res)
                    iconTint = ColorStateList.valueOf(ui.color(fg))
                }
            }
            setOnClickListener {
                when {
                    !b.action.isNullOrBlank() -> actions.action(b.action)
                    b.style == "primary" || !b.to.isNullOrBlank() || !b.toChoice.isNullOrEmpty() -> {
                        if (b.style != "primary" && !b.to.isNullOrBlank()) {
                            actions.navigate(b.to)
                        } else {
                            val originalText = b.text
                            isEnabled = false
                            text = ScreenLabels.resolve("{{common.please_wait}}", actions.currentLanguage())
                            actions.primary(b, state) {
                                isEnabled = true
                                text = originalText
                            }
                        }
                    }
                    else -> actions.message(ctx.getString(R.string.example_only))
                }
            }
        }
        return button
    }

    private fun buildCta(b: Block): View = ui.vertical().apply {
        b.children.orEmpty().forEachIndexed { index, child ->
            addView(button(child), ui.lp(top = if (index == 0) 0 else 4))
        }
    }

    private fun balance(b: Block): View = ui.vertical().apply {
        background = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(ui.color(R.color.primary), ui.color(R.color.card_dark))).apply {
            cornerRadius = ui.dpf(24f)
        }
        setPadding(ui.dp(20), ui.dp(20), ui.dp(20), ui.dp(20))
        addView(ui.text(b.tag, 12f, R.color.white).apply { alpha = 0.8f })
        addView(ui.text(b.amount, 32f, R.color.white, bold = true), ui.lp(top = 4, bottom = 4))
        addView(ui.text(b.sub, 11f, R.color.white).apply { alpha = 0.75f })
    }

    private fun wallets(b: Block): View = ui.horizontal(centered = false).apply {
        weightSum = (b.items?.size ?: 1).toFloat()
        b.items.orEmpty().forEachIndexed { index, item ->
            val card = ui.vertical().apply {
                background = ui.rounded(R.color.surface, 14, R.color.border)
                setPadding(ui.dp(14), ui.dp(12), ui.dp(14), ui.dp(12))
            }
            val head = ui.horizontal()
            head.addView(badge(item.cur, item.style))
            head.addView(ui.text(item.label, 12f, R.color.text_muted), ui.lp(width = ViewGroup.LayoutParams.WRAP_CONTENT, start = 8))
            card.addView(head, ui.lp(bottom = 8))
            card.addView(ui.text(item.amount, 17f, bold = true))
            addView(card, ui.lp(width = 0, weight = 1f, start = if (index == 0) 0 else 5, end = if (index == (b.items?.lastIndex ?: 0)) 0 else 5))
        }
    }

    private fun quick(b: Block): View = ui.horizontal(centered = false).apply {
        weightSum = (b.items?.size ?: 1).toFloat()
        b.items.orEmpty().forEach { item: Item ->
            val cell = ui.vertical().apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(0, ui.dp(4), 0, ui.dp(4))
                ui.ripple(this)
                if (!item.to.isNullOrBlank()) setOnClickListener { actions.navigate(item.to) }
            }
            cell.addView(
                FrameLayout(ctx).apply {
                    background = ui.circle(R.color.primary_highlight)
                    addView(ui.icon(item.icon, 22, R.color.primary), FrameLayout.LayoutParams(ui.dp(22), ui.dp(22), Gravity.CENTER))
                },
                ui.lp(width = ui.dp(52), height = ui.dp(52), bottom = 6),
            )
            cell.addView(ui.text(item.label, 12f, R.color.text, bold = true).apply { gravity = Gravity.CENTER })
            addView(cell, ui.lp(width = 0, weight = 1f))
        }
    }

    private fun segmented(b: Block): View {
        val options = b.options.orEmpty()
        val bar = ui.horizontal().apply {
            background = ui.rounded(R.color.surface_offset, 12)
            setPadding(ui.dp(4), ui.dp(4), ui.dp(4), ui.dp(4))
        }
        val labels = options.map { ui.text(it, 13f, R.color.text_muted, bold = true).apply { gravity = Gravity.CENTER; setPadding(0, ui.dp(9), 0, ui.dp(9)) } }
        fun select(selected: Int) = labels.forEachIndexed { i, tv ->
            tv.background = if (i == selected) ui.rounded(R.color.surface, 9) else null
            tv.setTextColor(ui.color(if (i == selected) R.color.primary else R.color.text_muted))
        }
        labels.forEachIndexed { i, tv ->
            tv.setOnClickListener { select(i) }
            bar.addView(tv, ui.lp(width = 0, weight = 1f))
        }
        select(b.active)
        return bar
    }

    private fun pills(b: Block): View {
        val group = ChipGroup(ctx).apply { chipSpacingHorizontal = ui.dp(8); isSingleSelection = true }
        val targets = b.targets.orEmpty()
        val checkedStates = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        b.options.orEmpty().forEachIndexed { index, label ->
            val chip = Chip(ctx).apply {
                text = label
                isCheckable = targets.isEmpty()
                isCheckedIconVisible = false
                chipBackgroundColor = ColorStateList(checkedStates, intArrayOf(ui.color(R.color.primary), ui.color(R.color.surface)))
                setTextColor(ColorStateList(checkedStates, intArrayOf(ui.color(R.color.white), ui.color(if (targets.isEmpty()) R.color.text_muted else R.color.primary))))
                chipStrokeColor = ColorStateList.valueOf(ui.color(if (targets.isEmpty()) R.color.border else R.color.primary))
                chipStrokeWidth = ui.dpf(1f)
                isChecked = b.actives?.contains(index) == true
                if (targets.isNotEmpty()) setOnClickListener {
                    val target = targets.getOrNull(index)
                    if (target.isNullOrBlank()) actions.message(ctx.getString(R.string.example_only)) else actions.navigate(target)
                }
            }
            group.addView(chip)
        }
        return group
    }

    private fun progress(b: Block): View = LinearProgressIndicator(ctx).apply {
        isIndeterminate = false
        max = 100
        progress = b.percent.toInt()
        trackThickness = ui.dp(8)
        trackCornerRadius = ui.dp(4)
        setIndicatorColor(ui.color(R.color.primary))
        trackColor = ui.color(R.color.surface_offset2)
    }

    private fun chat(b: Block): View {
        val user = b.from == "user"
        return ui.text(b.text, 14f, if (user) R.color.white else R.color.text).apply {
            background = ui.rounded(if (user) R.color.primary else R.color.surface, 16, if (user) null else R.color.border)
            setPadding(ui.dp(14), ui.dp(10), ui.dp(14), ui.dp(10))
            maxWidth = (ctx.resources.displayMetrics.widthPixels * 0.78f).toInt()
        }
    }

    private fun chatParams(b: Block): LinearLayout.LayoutParams = ui.lp(width = ViewGroup.LayoutParams.WRAP_CONTENT, bottom = 10).apply {
        gravity = if (b.from == "user") Gravity.END else Gravity.START
    }

    private fun greeting(b: Block): View = ui.horizontal().apply {
        val texts = ui.vertical()
        texts.addView(ui.text(b.hello, 13f, R.color.text_muted))
        texts.addView(ui.text(b.name, 20f, bold = true))
        addView(texts, ui.lp(width = 0, weight = 1f))
        if (b.bell) addView(
            FrameLayout(ctx).apply {
                background = ui.circle(R.color.surface)
                addView(ui.icon("bell", 20, R.color.text), FrameLayout.LayoutParams(ui.dp(20), ui.dp(20), Gravity.CENTER))
            },
            ui.lp(width = ui.dp(40), height = ui.dp(40)),
        )
    }

    private val Block.bell: Boolean get() = t == "greeting"

    private fun sectionLink(b: Block): View = ui.horizontal().apply {
        addView(ui.text(b.text, 16f, bold = true), ui.lp(width = 0, weight = 1f))
        addView(ui.text(b.link, 13f, R.color.primary, bold = true).apply {
            setPadding(ui.dp(8), ui.dp(6), 0, ui.dp(6))
            if (!b.to.isNullOrBlank()) setOnClickListener { actions.navigate(b.to) }
        })
    }

    private fun languages(b: Block): View {
        val list = ui.vertical()
        languageRows.clear()
        b.items.orEmpty().forEach { item ->
            val code = (item.code ?: "fr").lowercase(Locale.ROOT)
            val row = ui.horizontal().apply {
                setPadding(ui.dp(16), ui.dp(14), ui.dp(16), ui.dp(14))
                ui.ripple(this)
            }
            row.addView(ui.text(item.name, 16f, bold = true), ui.lp(width = 0, weight = 1f))
            row.addView(ui.text(item.code?.uppercase(Locale.ROOT), 12f, R.color.text_faint), ui.lp(width = ViewGroup.LayoutParams.WRAP_CONTENT, end = 10))
            val tick = ui.icon("check_circle", 22, R.color.primary)
            row.addView(tick, ui.lp(width = ui.dp(22), height = ui.dp(22)))
            row.tag = tick
            languageRows += row to code
            row.setOnClickListener { state.languageCode = code; refreshLanguages() }
            list.addView(row, ui.lp(bottom = 10))
        }
        refreshLanguages()
        return list
    }

    private fun refreshLanguages() {
        languageRows.forEach { (view, code) ->
            val selected = code == state.languageCode
            view.background = if (selected) ui.rounded(R.color.primary_highlight, 14, R.color.primary, 2f) else ui.rounded(R.color.surface, 14, R.color.border, 1.5f)
            (view.tag as? View)?.visibility = if (selected) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun pinPad(b: Block): View {
        val entered = StringBuilder()
        val dots = List(4) { View(ctx) }
        fun refreshDots() = dots.forEachIndexed { i, v -> v.background = ui.circle(if (i < entered.length) R.color.primary else R.color.surface_offset2) }
        refreshDots()
        val box = ui.vertical().apply { gravity = Gravity.CENTER_HORIZONTAL }
        box.addView(
            FrameLayout(ctx).apply {
                background = ui.circle(R.color.primary_highlight)
                addView(ui.icon("lock", 28, R.color.primary), FrameLayout.LayoutParams(ui.dp(28), ui.dp(28), Gravity.CENTER))
            },
            ui.lp(width = ui.dp(64), height = ui.dp(64), top = 24, bottom = 16),
        )
        box.addView(ui.text(b.title, 20f, bold = true).apply { gravity = Gravity.CENTER }, ui.lp(bottom = 6))
        box.addView(ui.text(b.sub, 13f, R.color.text_muted).apply { gravity = Gravity.CENTER }, ui.lp(bottom = 20))
        val dotRow = ui.horizontal().apply { gravity = Gravity.CENTER }
        dots.forEach { dotRow.addView(it, ui.lp(width = ui.dp(14), height = ui.dp(14), start = 8, end = 8)) }
        box.addView(dotRow, ui.lp(bottom = 24))

        fun key(label: String?, iconName: String? = null, onClick: () -> Unit): View = FrameLayout(ctx).apply {
            if (label != null) {
                background = ui.circle(R.color.surface)
                addView(ui.text(label, 22f, bold = true).apply { gravity = Gravity.CENTER }, FrameLayout.LayoutParams(-1, -1))
            } else if (iconName != null) {
                addView(ui.icon(iconName, 24, R.color.primary), FrameLayout.LayoutParams(ui.dp(24), ui.dp(24), Gravity.CENTER))
            }
            if (label != null || iconName != null) {
                ui.ripple(this)
                setOnClickListener { onClick() }
            }
        }

        fun digit(d: String) {
            if (entered.length < 4) entered.append(d)
            refreshDots()
            if (entered.length == 4) actions.pinEntered()
        }
        val keys: List<View> = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9").map { d -> key(d) { digit(d) } } + listOf(
            key(null, "fingerprint") { actions.pinEntered() },
            key("0") { digit("0") },
            key(null, "chevron_left") { if (entered.isNotEmpty()) entered.deleteCharAt(entered.length - 1); refreshDots() },
        )
        keys.chunked(3).forEach { rowKeys ->
            val line = ui.horizontal().apply { gravity = Gravity.CENTER }
            rowKeys.forEach { line.addView(it, ui.lp(width = ui.dp(68), height = ui.dp(68), start = 10, end = 10, bottom = 12)) }
            box.addView(line, ui.lp())
        }
        if (!b.forgot.isNullOrBlank()) {
            box.addView(ui.text(b.forgot, 13f, R.color.primary, bold = true).apply {
                setPadding(ui.dp(12), ui.dp(12), ui.dp(12), ui.dp(12))
                setOnClickListener { actions.message(ctx.getString(R.string.example_only)) }
            }, ui.lp(width = ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        return box
    }
}
