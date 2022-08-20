package scheduler.receiver

import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import hlv.cute.todo.R
import model.Notification
import repo.dbRepoController.NotificationDBRepository
import scheduler.alarm.AlarmUtil
import utils.Constants
import utils.NotificationUtil
import javax.inject.Inject

class ReceiverController @Inject constructor() {

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var repository: NotificationDBRepository

    @Inject
    lateinit var alarmUtil: AlarmUtil

    @Inject
    lateinit var notificationUtil: NotificationUtil

    private var mustBeSetAgain = false

    fun handleWith(intent: Intent?) {
        if (intent == null)
            return

        var received = "Alarm received in normal mode."
        var succeed = "Normal mode notification send successfully."

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            received = "Alarm received after boot completed."
            succeed = "BootComplete mode notification send successfully."
            mustBeSetAgain = true
        }

        if (intent.action == "android.intent.action.TIME_SET") {
            received = "Alarm received after time & date changed."
            succeed = "Date Time mode notification send successfully."
            mustBeSetAgain = true
        }

        normalLog(received)

        if (mustBeSetAgain) {
            try {
                val notifications = repository.getAllNotificationsWithinThread()
                if (notifications != null) {
                    for ((id, _, _, _, arriveDate) in notifications) {
                        //AlarmUtil.with(context.getApplicationContext()).cancelAlarm(notification.getId());
                        alarmUtil.setAlarm(id, arriveDate)
                    }
                }
            } catch (ignored: InterruptedException) {
            }

            mustBeSetAgain = false
        }

        val notifID = intent.getIntExtra(Constants.Keys.NOTIF_ID_KEY, 0)

        if (notifID == 0)
            return

        var notification: Notification? = null

        try {
            notification = repository.getNotificationWithinThread(notifID.toLong())
        } catch (ignored: InterruptedException) {
        }

        if (notification != null) {
            val title = context.getString(R.string.notification_header)
            var content = notification.content
            val summary = content

            if (content != null && content.trim().length > 100)
                content = content.substring(0, 100).trim() + context.getString(R.string.ellipsis)

            notificationUtil.makeNotification(
                title,
                summary,
                context.getString(R.string.notification_content, content),
                notification.priority,
                notifID
            )

            successLog(succeed)
        }
    }

    private fun successLog(log: String) {
        Log.i(Constants.Tags.SUCCESS_NOTIF_ALARM, log)
    }

    private fun normalLog(log: String) {
        Log.i(Constants.Tags.ALARM_TAG, log)
    }
}