package scheduler.alarm

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

        val pendingIntent = getSafePendingIntentBroadCast(context, notificationID, intent)

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

        val pendingIntent = getSafePendingIntentBroadCast(context, notificationID, intent)

        alarmManager?.cancel(pendingIntent)
    }

    /**
     *  Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE
     *  be specified when creating a PendingIntent.
     *  Strongly consider using FLAG_IMMUTABLE,
     *  only use FLAG_MUTABLE if some functionality depends on the PendingIntent being mutable, e.g.
     *  if it needs to be used with inline replies or bubbles.
     */
    private fun getSafePendingIntentBroadCast(
        context: Context,
        requestCode: Int,
        intent: Intent
    ): PendingIntent {
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            //noinspection UnspecifiedImmutableFlag
            PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        return pendingIntent
    }

    fun getSafePendingIntentActivity(
        context: Context,
        requestCode: Int,
        intent: Intent
    ): PendingIntent {
        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context, requestCode, intent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            //noinspection UnspecifiedImmutableFlag
            PendingIntent.getActivity(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        return pendingIntent
    }

}