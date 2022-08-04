package ui.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color.BLACK
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.PaintDrawable
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import hlv.cute.todo.R
import javax.inject.Inject


@FragmentScoped
class PopupMaker @Inject constructor(
    @ActivityContext private val context: Context
) {

    @SuppressLint("RestrictedApi")
    fun showMenu(
        anchor: View,
        @MenuRes menuRes: Int,
        isRtl: Boolean = true,
        gravity: Int = Gravity.START,
        onMenuItemClick: (MenuItem) -> Unit
    ): PopupMenu {

        val directionContext: Context =
            ContextThemeWrapper(context, if (isRtl) R.style.RTLStyle else R.style.LTRStyle)


        val popup = PopupMenu(directionContext, anchor, gravity).apply {
            inflate(menuRes)

            setOnMenuItemClickListener { menuItem ->
                onMenuItemClick(menuItem)
                return@setOnMenuItemClickListener true
            }
        }

        if (popup.menu is MenuBuilder) {
            val menuBuilder = (popup.menu as MenuBuilder).apply {
                setOptionalIconsVisible(true)
            }

            for (item in menuBuilder.visibleItems) {
                val iconMarginPx =
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        3.toFloat(),
                        directionContext.resources.displayMetrics
                    ).toInt()

                if (item.icon != null) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0)
                    } else {
                        item.icon =
                            object : InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx, 0) {
                                override fun getIntrinsicWidth(): Int {
                                    return intrinsicHeight + iconMarginPx + iconMarginPx
                                }
                            }
                    }
                }
            }
        }

        /*val menuPopupHelper = MenuPopupHelper(
            directionContext,
            popup.menu as MenuBuilder,
            anchor,
            false,
            0,
            R.style.PopupMenuMoreCentralized
        )
        menuPopupHelper.show(233, 321)*/

        popup.show()
        return popup
    }

    fun PopupMenu.changeTextColorOfItem(position: Int, title: String, color: Int) {
        val item = this.menu.getItem(position)
        this.changeTextColorOfItem(item, title, color)
    }

    fun PopupMenu.changeTextColorOfItem(item: MenuItem, title: String, color: Int) {
        val span = SpannableString(title)
        span.setSpan(ForegroundColorSpan(color), 0, span.length, 0)
        item.title = span
    }

    fun applyDim(parent: ViewGroup, dimAmount: Int) {
        val dim: Drawable = ColorDrawable(BLACK)
        dim.setBounds(0, 0, parent.width, parent.height)
        dim.alpha = dimAmount
        val overlay = parent.overlay
        overlay.add(dim)
    }

    fun clearDim(parent: ViewGroup) {
        val overlay = parent.overlay
        overlay.clear()
    }

    /*  private val Number.dp2px: Int
          get() =
              TypedValue.applyDimension(
                  TypedValue.COMPLEX_UNIT_DIP,
                  this.toFloat(),
                  context.resources?.displayMetrics
              ).toInt()*/
}

class RoundedRectDrawable(color: Int, radius: Float) : PaintDrawable(color) {
    init {
        setCornerRadius(radius)
    }
}