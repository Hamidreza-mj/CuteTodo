package utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import dagger.hilt.android.qualifiers.ApplicationContext
import hlv.cute.todo.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val toasts = mutableListOf<Toast>()

    fun toast(message: String?, isLongDuration: Boolean = false) {
        Handler(Looper.getMainLooper()).post {
            for (toast in toasts)
                toast.cancel()

            toasts.clear()

            val toast = Toast(context)

            @SuppressLint("InflateParams")
            val view =
                LayoutInflater.from(context).inflate(R.layout.toast, null, false)

            val txtToast = view.findViewById<TextView>(R.id.toast_txt)

            txtToast.text = message
            txtToast.typeface =
                Typeface.createFromAsset(context.assets, "font/vazir_regular.ttf")
            toast.duration = if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            toast.setGravity(Gravity.BOTTOM, 0, 100)
            toast.view = view
            toast.show()
            toasts.add(toast)
        }
    }

    fun successToast(message: String?, isLongDuration: Boolean = false) {
        Handler(Looper.getMainLooper()).post {
            for (toast in toasts)
                toast.cancel()

            toasts.clear()

            val toast = Toast(context)
            @SuppressLint("InflateParams") val view = LayoutInflater.from(context)
                .inflate(R.layout.toast_lottie, null, false)

            val txtToast = view.findViewById<TextView>(R.id.toast_txt)
            txtToast.text = message
            txtToast.typeface =
                Typeface.createFromAsset(context.assets, "font/vazir_regular.ttf")

            toast.duration = if (isLongDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            toast.setGravity(Gravity.BOTTOM, 0, 100)
            toast.view = view
            toast.show()
            val animationView = view.findViewById<LottieAnimationView>(R.id.success)
            animationView.playAnimation()
            toasts.add(toast)
        }
    }


}