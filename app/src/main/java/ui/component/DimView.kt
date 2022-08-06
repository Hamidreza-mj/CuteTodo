package ui.component

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import kotlin.math.abs


@FragmentScoped
class DimView @Inject constructor(
    @ActivityContext private val context: Context
) {

    fun applyBlurDim(parent: ViewGroup, nonDimView: View?) {
        try {
            val blurredBitmap: Bitmap = makeBlurredBitmap(parent)
            val blurDimDrawable = convertBitmapToDrawable(blurredBitmap, context).apply {
                setBounds(0, 0, parent.width, parent.height)
            }

            val overlay = parent.overlay
            overlay.add(blurDimDrawable)

            //fade(parent)

            nonDimView?.let {
                try {
                    val nonDimBitmap = it.drawToBitmap()

                    val nonDimViewPos = IntArray(2)
                    it.getLocationInWindow(nonDimViewPos)
                    val (x, y) = nonDimViewPos

                    val nonDimDrawable = convertBitmapToDrawable(nonDimBitmap, context).apply {
                        val offsetHeight = abs(getStatusBarHeight())

                        val rect =
                            Rect(x, y - offsetHeight, x + it.width, y + it.height - offsetHeight)

                        bounds = rect
                    }

                    overlay.add(nonDimDrawable)
                } catch (ignored: Exception) {
                }
            }

        } catch (e: Exception) {
            applyDim(parent)

            nonDimView?.let {
                try {
                    val nonDimBitmap = it.drawToBitmap()

                    val nonDimViewPos = IntArray(2)
                    it.getLocationInWindow(nonDimViewPos)
                    val (x, y) = nonDimViewPos

                    val nonDimDrawable = convertBitmapToDrawable(nonDimBitmap, context).apply {
                        val offsetHeight = abs(getStatusBarHeight())

                        val rect =
                            Rect(x, y - offsetHeight, x + it.width, y + it.height - offsetHeight)

                        bounds = rect
                    }

                    parent.overlay.add(nonDimDrawable)
                } catch (ignored: Exception) {
                }
            }
        }
    }

    fun applyDim(parent: ViewGroup, dimAmount: Int = 80) {
        val dimDrawable = makeDimOpacity(dimAmount)

        dimDrawable.setBounds(0, 0, parent.width, parent.height)
        val overlay = parent.overlay
        overlay.add(dimDrawable)
        //fade(parent)
    }

    private fun makeBlurredBitmap(view: View): Bitmap {
        val normalBitmap: Bitmap = view.drawToBitmap()
        return BlurBuilder.blur(context, normalBitmap)
    }

    private fun convertBitmapToDrawable(bitmap: Bitmap, context: Context): BitmapDrawable {
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun makeDimOpacity(dimAmount: Int): ColorDrawable {
        val dimDrawable = ColorDrawable(Color.BLACK).apply {
            alpha = dimAmount
        }

        return dimDrawable
    }

    fun clearDim(parent: ViewGroup) {
        val overlay = parent.overlay
        overlay.clear()
        //fade(parent)
    }

    private fun fade(view: View) {
        view.alpha = 1f
        view.animate().alpha(1f).setDuration(700).start()
    }

    private fun getStatusBarHeight(): Int {
        val rectangle = Rect()

        val window = (context as Activity).window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)

        val statusBarHeight = rectangle.top
        //val contentViewTop: Int = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        //val titleBarHeight = contentViewTop - statusBarHeight

        return statusBarHeight
    }

}