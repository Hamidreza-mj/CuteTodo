package ui.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.InsetDrawable
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
    @ActivityContext private val context: Context,
    private val dimView: DimView
) {

    private var clicked = false

    private var viewToDim: ViewGroup? = null

    private var isNestedMenu = false

    @SuppressLint("RestrictedApi")
    fun showMenu(
        anchor: View,
        coordinatePoint: Point? = null,
        viewToDim: ViewGroup? = null,
        nonDimItem: View? = null,
        @MenuRes menuRes: Int,
        isRtl: Boolean = true,
        isFirstShow: Boolean = true,
        gravity: Int = Gravity.START,
        onMenuItemClick: (MenuItem) -> Unit,
        onDismiss: (() -> Unit)? = null
    ): PopupMenu? {

        if (clicked) return null

        clicked = true

        this.viewToDim = viewToDim

        viewToDim?.let {
            if (!isNestedMenu && isFirstShow)
                dimView.applyBlurDim(it, nonDimItem)
        }


        val directionContext: Context =
            ContextThemeWrapper(context, if (isRtl) R.style.RTLStyle else R.style.LTRStyle)


        var tempAnchor: View = anchor
        var root: ViewGroup? = null

        coordinatePoint?.let {
            root =
                (context as Activity).window.decorView.findViewById(android.R.id.content) as ViewGroup

            tempAnchor = View(context)
            tempAnchor.layoutParams = ViewGroup.LayoutParams(0, 0)
            tempAnchor.setBackgroundColor(Color.TRANSPARENT)

            root?.addView(tempAnchor)

            tempAnchor.x = it.x.toFloat()
            tempAnchor.y = it.y.toFloat()
        }


        val popup = PopupMenu(directionContext, tempAnchor, gravity).apply {
            inflate(menuRes)

            setOnMenuItemClickListener { menuItem ->
                menuItem.isEnabled = false //to avoid multiple click

                onMenuItemClick(menuItem)
                return@setOnMenuItemClickListener true
            }

            setOnDismissListener {
                clicked = false

                root?.removeView(tempAnchor)

                viewToDim?.let {
                    if (!isNestedMenu)
                        dimView.clearDim(it)

                    isNestedMenu = false
                }

                onDismiss?.invoke()
                dismiss()
            }
        }

        if (popup.menu is MenuBuilder) {
            val menuBuilder = (popup.menu as MenuBuilder).apply {
                setOptionalIconsVisible(true)
            }

            for (item in menuBuilder.visibleItems) {
                val iconMarginPx = 5.dp2px

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

    fun prepareForNestedMenu() {
        isNestedMenu = true
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

    fun PopupMenu.setVisibilityMenuItem(position: Int, isVisible: Boolean) {
        val item = this.menu.getItem(position)
        this.setVisibilityMenuItem(item, isVisible)
    }

    fun PopupMenu.setVisibilityMenuItem(item: MenuItem, isVisible: Boolean) {
        item.isVisible = isVisible
    }

    fun PopupMenu.setEnableMenuItem(position: Int, isEnable: Boolean) {
        val item = this.menu.getItem(position)
        this.setEnableMenuItem(item, isEnable)
    }

    fun PopupMenu.setEnableMenuItem(item: MenuItem, isEnable: Boolean) {
        item.isEnabled = isEnable
    }

    fun clearAllDim() {
        viewToDim?.let {
            dimView.clearDim(it)
        }
    }

    fun releaseClick() {
        clicked = false
    }

    private val Number.dp2px: Int
        get() =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                this.toFloat(),
                context.resources?.displayMetrics
            ).toInt()
}