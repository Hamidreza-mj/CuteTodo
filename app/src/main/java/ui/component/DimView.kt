package ui.component

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

            nonDimView?.let {
                try {
                    val nonDimBitmap = it.drawToBitmap()

                    val nonDimViewPos = IntArray(2)
                    it.getLocationInWindow(nonDimViewPos)
                    val x = nonDimViewPos[0]
                    val y = nonDimViewPos[1]

                    val nonDimDrawable = convertBitmapToDrawable(nonDimBitmap, context).apply {
                        val rect = Rect(x, y - 80, x + it.width, y + it.height - 80)
                        bounds = rect
                    }

                    overlay.add(nonDimDrawable)
                } catch (ignored: Exception) {
                }
            }

        } catch (e: Exception) {
            applyDim(parent)
        }
    }

    fun applyDim(parent: ViewGroup, dimAmount: Int = 50) {
        val dimDrawable = makeDimOpacity(dimAmount)

        dimDrawable.setBounds(0, 0, parent.width, parent.height)
        val overlay = parent.overlay
        overlay.add(dimDrawable)
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
    }

}