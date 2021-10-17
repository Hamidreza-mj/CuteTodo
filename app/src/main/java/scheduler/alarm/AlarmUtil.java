package scheduler.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import hlv.cute.todo.App;
import hlv.cute.todo.R;
import scheduler.receiver.NotificationReceiver;
import utils.Constants;

public class AlarmUtil {
    private static AlarmUtil alarmUtil;

    private AlarmUtil() {
    }

    public static AlarmUtil get() {
        if (alarmUtil == null)
            alarmUtil = new AlarmUtil();

        return alarmUtil;
    }

    public void setAlarm(int notificationID, String content, long timeAt) {
        if (timeAt < System.currentTimeMillis()) //if the time to be set has passed, don't need to set alarm
            return;

        AlarmManager alarmManager = (AlarmManager) App.get().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(App.get().getApplicationContext(), NotificationReceiver.class);

        intent.putExtra(Constants.Keys.NOTIF_ID_KEY, notificationID);
        intent.putExtra(Constants.Keys.NOTIF_CONTENT_KEY, App.get().getApplicationContext().getString(R.string.notification_content, content));

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(App.get().getApplicationContext(), notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(timeAt, pendingIntent), pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeAt, pendingIntent);
        else
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeAt, pendingIntent);
    }

    public void cancelAlarm(int notificationID) {
        setAlarm(notificationID, "", 0);
        AlarmManager alarmManager = (AlarmManager) App.get().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(App.get().getApplicationContext(), NotificationReceiver.class);

//        intent.putExtra(Constants.Keys.NOTIF_ID_KEY, notificationID);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(App.get().getApplicationContext(), notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}
