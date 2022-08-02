package scheduler.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import scheduler.receiver.NotificationReceiver
import utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var alarmManager: AlarmManager? = null

    init {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setAlarm(notificationID: Int, timeAt: Long) {
        //if the time to be set has passed, don't need to set alarm
        //if timeAt = 0, set alarm (for canceling)
        if (timeAt < System.currentTimeMillis())
            return

        val intent = Intent(context, NotificationReceiver::class.java)

        intent.putExtra(Constants.Keys.NOTIF_ID_KEY, notificationID)
        @SuppressLint("UnspecifiedImmutableFlag")
        val pendingIntent = PendingIntent.getBroadcast(
            context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        //alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timeAt, pendingIntent), pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeAt,
                pendingIntent
            )
        } else {
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, timeAt, pendingIntent)
        }
    }

    /**
     * for canceling alarm the context and id must be same.
     * better idea use application context
     *
     * @param notificationID id of pending intent
     */
    fun cancelAlarm(notificationID: Int) {
        //setAlarm(notificationID, "", 0);
        val intent = Intent(context, NotificationReceiver::class.java)

        @SuppressLint("UnspecifiedImmutableFlag")
        val pendingIntent = PendingIntent.getBroadcast(
            context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager?.cancel(pendingIntent)
    }
}