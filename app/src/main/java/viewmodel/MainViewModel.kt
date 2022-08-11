package viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.yandex.metrica.push.YandexMetricaPush
import utils.Constants

class MainViewModel : ViewModel() {

    var openingType: OpeningType = OpeningType.NORMAL

    private fun openFromScheduledNotification(intent: Intent): Boolean {
        if (!intent.hasExtra(Constants.Keys.NOTIF_ID_DETAIL))
            return false

        val notifId = intent.getIntExtra(Constants.Keys.NOTIF_ID_DETAIL, 0)

        if (notifId == 0)
            return false

        return true
    }

    private fun openFromShareMenu(intent: Intent): Boolean {
        if (intent.action == Intent.ACTION_SEND) {
            if (intent.type == "text/plain")
                return true
        }

        return false
    }

    private fun openFromShortcutMenu(intent: Intent): Boolean {
        if (intent.action == "cute.todo.from.shortcut.add")
            return true

        return false
    }

    private fun openFromPopupTextSelection(intent: Intent): Boolean {
        if (intent.action == Intent.ACTION_PROCESS_TEXT) {
            if (intent.type == "text/plain")
                return true
        }

        return false
    }

    private fun openFromPush(intent: Intent): Boolean {
        if (intent.action == YandexMetricaPush.OPEN_DEFAULT_ACTIVITY_ACTION)
            return true

        return false
    }

    fun handleOpeningType(intent: Intent) {
        intent.let {
            if (openFromScheduledNotification(it)) {
                openingType = OpeningType.SCHEDULED_NOTIF
                return
            }

            if (openFromShareMenu(it)) {
                openingType = OpeningType.SHARE_MENU
                return
            }

            if (openFromShortcutMenu(it)) {
                openingType = OpeningType.SHORTCUT_MENU
                return
            }

            if (openFromPopupTextSelection(it)) {
                openingType = OpeningType.POPUP_TEXT_SELECTION
                return
            }

            if (openFromPush(it)) {
                openingType = OpeningType.PUSH_NOTIF
                return
            }

            openingType = OpeningType.NORMAL
        }
    }

    enum class OpeningType {
        SCHEDULED_NOTIF,
        SHARE_MENU,
        SHORTCUT_MENU,
        POPUP_TEXT_SELECTION,
        PUSH_NOTIF,
        NORMAL
    }

}