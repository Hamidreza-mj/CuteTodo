package utils

import android.graphics.Paint
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.widget.TextView

object TextHelper {

    fun addLineThrough(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    fun removeLineThrough(textView: TextView) {
        //set all text paint flags, except of Paint.STRIKE_THRU_TEXT_FLAG
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    fun fromHtml(html: String?): Spanned {
        //return an empty spannable if the html is null
        if (html == null)
            return SpannableString("")


        //FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
        //we are using this flag to give a consistent behaviour
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
    }
}