package utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.ContextCompat

interface ResourceProvider {
    //val isRtl: Boolean

    @ColorInt
    fun getColor(@ColorRes resId: Int): Int

    fun getColorStateList(@ColorRes resId: Int): ColorStateList

    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg args: Any): String

    fun getDimen(@DimenRes resId: Int): Float

    fun getDimenInt(@DimenRes resId: Int): Int

    fun getDrawable(@DrawableRes resId: Int): Drawable

    //... other things, above methods just for the example
}

class AppResourcesProvider(
    private val context: Context
) : ResourceProvider {

    @ColorInt
    override fun getColor(resId: Int) = ContextCompat.getColor(context, resId)

    override fun getColorStateList(resId: Int) = context.getColorStateList(resId)

    override fun getString(resId: Int) = context.getString(resId)

    override fun getString(resId: Int, vararg args: Any) = context.getString(resId, *args)

    override fun getDimen(resId: Int) = context.resources.getDimension(resId)

    override fun getDimenInt(resId: Int) = context.resources.getDimensionPixelSize(resId)

    override fun getDrawable(resId: Int) = ContextCompat.getDrawable(context, resId)!!

    //implementation for another methods

}