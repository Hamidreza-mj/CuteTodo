package ui.component

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class UiToolkit @Inject constructor(
    @ActivityContext private val context: Context
) {

    val Number.dp2px: Float
        get() =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                context.resources?.displayMetrics
            )


    val Number.dp2pxInt: Int
        get() =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                context.resources?.displayMetrics
            ).toInt()


    val statusBarHeight: Int
        get() {
            val rectangle = Rect()

            val window = (context as Activity).window
            window.decorView.getWindowVisibleDisplayFrame(rectangle)

            val statusBarHeight = rectangle.top
            //val contentViewTop: Int = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
            //val titleBarHeight = contentViewTop - statusBarHeight

            return statusBarHeight
        }

    val displayWidth: Int
        get() = context.resources.displayMetrics.widthPixels

    val displayHeight: Int
        get() = context.resources.displayMetrics.heightPixels
}