package scheduler.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Objects;

import hlv.cute.todo.R;
import utils.Constants;
import utils.NotificationUtil;

/**
 * this class is receiver to rehandle Notification after date and time of deivce changed
 */
public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.TIME_SET")) {
            int notifID = intent.getIntExtra(Constants.Keys.NOTIF_ID_KEY, 1);
            String title = context.getString(R.string.notification_header);
            String content = intent.hasExtra(Constants.Keys.NOTIF_CONTENT_KEY) ? intent.getStringExtra(Constants.Keys.NOTIF_CONTENT_KEY) : "";

            Log.i(Constants.Tags.ALARM_TAG, "Alarm received after time & date changed.");

            NotificationUtil notificationUtil = new NotificationUtil(context);
            notificationUtil.makeNotification(title, content, content, notifID);
        }
    }

}
