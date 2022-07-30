package utils

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyboardUtil {

    fun showKeyboard(context: Context) {
        try {
            @Suppress("DEPRECATION") //toggleSoftInput is deprecated
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun EditText.focusAndShowKeyboard(context: Context) {
        try {
            this.postDelayed({
                this.requestFocus()

                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(this, 0)

            }, 500)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun hideKeyboard(activity: Activity) {
        try {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if (activity.currentFocus != null && activity.currentFocus!!.windowToken != null) {

                (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}