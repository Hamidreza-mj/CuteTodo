package controller

import android.content.Intent
import com.yandex.metrica.push.YandexMetricaPush
import dagger.hilt.android.scopes.ViewModelScoped
import utils.Constants
import javax.inject.Inject

@ViewModelScoped
class OpenAppIntentController @Inject constructor() {

    fun getOpeningTypeWithIntent(intent: Intent): OpeningType {
        intent.let {
            if (openFromScheduledNotification(it))
                return OpeningType.SCHEDULED_NOTIF

            if (openFromShareMenu(it))
                return OpeningType.SHARE_MENU

            if (openFromShortcutMenu(it))
                return OpeningType.SHORTCUT_MENU

            if (openFromPopupTextSelection(it))
                return OpeningType.POPUP_TEXT_SELECTION

            if (openFromPush(it))
                return OpeningType.PUSH_NOTIF


            return OpeningType.NORMAL
        }
    }

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

    enum class OpeningType {
        SCHEDULED_NOTIF,
        SHARE_MENU,
        SHORTCUT_MENU,
        POPUP_TEXT_SELECTION,
        PUSH_NOTIF,
        NORMAL
    }

}