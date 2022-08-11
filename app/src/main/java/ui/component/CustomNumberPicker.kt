package ui.component

import android.content.Context
import android.widget.NumberPicker
import android.view.ViewGroup
import android.widget.TextView
import hlv.cute.todo.R
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CustomNumberPicker : NumberPicker {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun addView(child: View) {
        super.addView(child)
        updateView(child)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        updateView(child)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        super.addView(child, params)
        updateView(child)
    }

    private fun updateView(view: View) {
        if (view is TextView) {
            view.setTextColor(ContextCompat.getColor(context, R.color.black))
            view.typeface =
                Typeface.createFromAsset(resources.assets, "font/vazir_rd_fd_medium.ttf")

            view.textSize = 20f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                view.setAutoSizeTextTypeUniformWithConfiguration(8, 20, 1, 1)
        }
    }
}