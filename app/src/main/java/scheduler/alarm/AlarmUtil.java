package scheduler.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import hlv.cute.todo.R;
import scheduler.receiver.NotificationReceiver;
import utils.Constants;

public class AlarmUtil {
    @SuppressLint("StaticFieldLeak")
    private static AlarmUtil alarmUtil;
    private static AlarmManager alarmManager;
    private final Context context;

    private AlarmUtil(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static AlarmUtil with(Context context) {
        if (alarmUtil == null)
            alarmUtil = new AlarmUtil(context);

        return alarmUtil;
    }

    public void setAlarm(int notificationID, long timeAt) {
        //if the time to be set has passed, don't need to set alarm
        //if timeAt = 0, set alarm (for canceling)
        if (timeAt < System.currentTimeMillis())
            return;

        Intent intent = new Intent(context, NotificationReceiver.class);

        intent.putExtra(Constants.Keys.NOTIF_ID_KEY, notificationID);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timeAt, pendingIntent), pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAt, pendingIntent);
        else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAt, pendingIntent);
    }

    /**
     * for canceling alarm the context and id must be same.
     * better idea use application context
     *
     * @param notificationID id of pending intent
     */
    public void cancelAlarm(int notificationID) {
//        setAlarm(notificationID, "", 0);
        Intent intent = new Intent(context, NotificationReceiver.class);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

}
