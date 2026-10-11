package com.switsh.app.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.switsh.app.R
import kotlin.math.roundToInt

/** Small helpers shared by the programmatic views built from the prototype blocks. */
class UiKit(val ctx: Context) {
    private val density = ctx.resources.displayMetrics.density

    fun dp(value: Int): Int = (value * density).roundToInt()
    fun dpf(value: Float): Float = value * density
    fun color(@ColorRes id: Int): Int = ContextCompat.getColor(ctx, id)

    fun rounded(
        @ColorRes fill: Int?,
        radiusDp: Int,
        @ColorRes stroke: Int? = null,
        strokeDp: Float = 1f,
        dashed: Boolean = false,
    ): GradientDrawable = GradientDrawable().apply {
        cornerRadius = dpf(radiusDp.toFloat())
        if (fill != null) setColor(color(fill)) else setColor(android.graphics.Color.TRANSPARENT)
        if (stroke != null) {
            if (dashed) setStroke(dpf(strokeDp).roundToInt(), color(stroke), dpf(6f), dpf(4f))
            else setStroke(dpf(strokeDp).roundToInt(), color(stroke))
        }
    }

    fun circle(@ColorRes fill: Int): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color(fill))
    }

    fun text(
        value: CharSequence?,
        sizeSp: Float = 14f,
        @ColorRes color: Int = R.color.text,
        bold: Boolean = false,
    ): TextView = TextView(ctx).apply {
        text = value
        textSize = sizeSp
        setTextColor(color(color))
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

    fun iconRes(name: String?): Int {
        if (name.isNullOrBlank()) return 0
        val snake = name.replace(Regex("[A-Z]")) { "_" + it.value.lowercase() }
        return ctx.resources.getIdentifier("ic_$snake", "drawable", ctx.packageName)
    }

    fun icon(name: String?, sizeDp: Int = 20, @ColorRes tint: Int = R.color.primary): ImageView =
        ImageView(ctx).apply {
            val res = iconRes(name)
            if (res != 0) setImageResource(res)
            ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color(tint)))
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            layoutParams = LinearLayout.LayoutParams(dp(sizeDp), dp(sizeDp))
        }

    fun vertical(): LinearLayout = LinearLayout(ctx).apply { orientation = LinearLayout.VERTICAL }

    fun horizontal(centered: Boolean = true): LinearLayout = LinearLayout(ctx).apply {
        orientation = LinearLayout.HORIZONTAL
        if (centered) gravity = Gravity.CENTER_VERTICAL
    }

    fun lp(
        width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
        height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        weight: Float = 0f,
        top: Int = 0,
        bottom: Int = 0,
        start: Int = 0,
        end: Int = 0,
    ): LinearLayout.LayoutParams = LinearLayout.LayoutParams(width, height, weight).apply {
        setMargins(dp(start), dp(top), dp(end), dp(bottom))
    }

    fun ripple(view: View) {
        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        ctx.obtainStyledAttributes(attrs).also {
            view.foreground = it.getDrawable(0)
            it.recycle()
        }
    }
}
