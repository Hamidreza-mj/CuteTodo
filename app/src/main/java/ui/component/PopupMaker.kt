package ui.component

import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class PopupMaker @Inject constructor(
    @ActivityContext private val context: Context
) {

    fun showMenu(anchor: View, @MenuRes menuRes: Int, onMenuItemClick: (MenuItem) -> Unit) {
        val popup = PopupMenu(context, anchor).apply {
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
                        TypedValue.COMPLEX_UNIT_DIP, 2.toFloat(), context.resources.displayMetrics
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