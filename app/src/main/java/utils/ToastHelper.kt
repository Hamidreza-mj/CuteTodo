package utils

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import hlv.cute.todo.App
import hlv.cute.todo.R

class ToastHelper private constructor() {

    companion object {
        private val TOASTS = ArrayList<Toast>()
        private var toastHelper: ToastHelper? = null

        @JvmStatic
        fun get(): ToastHelper {
            if (toastHelper == null)
                toastHelper = ToastHelper()

            return toastHelper!!
        }
    }

    /**
     * show toast in normal / long duration
     *
     * @param message        message that show in toast
     * @param isLongDuration default is false, to show toast longer that normal mode you must set it true
     */
    /**
     * show toast with normal duration
     *
     * @param message message that show in toast
     */
    @JvmOverloads
    fun toast(message: String?, isLongDuration: Boolean = false) {
        Handler(Looper.getMainLooper()).post {
            for (toast in TOASTS)
                toast.cancel()

            TOASTS.clear()

            val toast = Toast(App.get()!!.appContext)

            @SuppressLint("InflateParams")
            val view =
                LayoutInflater.from(App.get()!!.appContext).inflate(R.layout.toast, null, false)

            val txtToast = view.findViewById<TextView>(R.id.toast_txt)

            txtToast.text = message
            txtToast.typeface =
                Typeface.createFromAsset(App.get()!!.appContext.assets, "font/vazir_regular.ttf")
            toast.duration = if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            toast.setGravity(Gravity.BOTTOM, 0, 100)
            toast.view = view
            toast.show()
            TOASTS.add(toast)
        }
    }

    @JvmOverloads
    fun successToast(message: String?, isLongDuration: Boolean = false) {
        Handler(Looper.getMainLooper()).post {
            for (toast in TOASTS)
                toast.cancel()

            TOASTS.clear()

            val toast = Toast(App.get()!!.appContext)
            @SuppressLint("InflateParams") val view = LayoutInflater.from(App.get()!!.appContext)
                .inflate(R.layout.toast_lottie, null, false)

            val txtToast = view.findViewById<TextView>(R.id.toast_txt)
            txtToast.text = message
            txtToast.typeface =
                Typeface.createFromAsset(App.get()!!.appContext.assets, "font/vazir_regular.ttf")

            toast.duration = if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            toast.setGravity(Gravity.BOTTOM, 0, 100)
            toast.view = view
            toast.show()
            val animationView = view.findViewById<LottieAnimationView>(R.id.success)
            animationView.playAnimation()
            TOASTS.add(toast)
        }
    }


}