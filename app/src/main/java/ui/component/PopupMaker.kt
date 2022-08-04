package ui.component

import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
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
            menuInflater.inflate(menuRes, menu)

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

}

/*private fun setupPopupWindow() {
      val inflater =
          activity?.applicationContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

      val popUpview = inflater.inflate(R.layout.popup, null) //parent view with android:translationZ="15dp"


      val txtCat: TextView = popUpview!!.findViewById(R.id.txtCategories)

      val width =
          (resources.displayMetrics?.widthPixels)?.let { displayWidth ->
              (displayWidth * 0.5).toInt()
          } ?: 300

      popupWindow = PopupWindow(popUpview, width, RelativeLayout.LayoutParams.WRAP_CONTENT, true)

      popupWindow?.contentView?.setOnClickListener {
          popupWindow?.dismiss()
      }
  }*/