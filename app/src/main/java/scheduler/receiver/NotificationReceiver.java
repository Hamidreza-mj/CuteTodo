package scheduler.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import hlv.cute.todo.R;
import utils.Constants;
import utils.NotificationUtil;

/**
 * this class is default receiver to handle Notification in normal mode
 */
public class NotificationReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        int notifID = intent.getIntExtra(Constants.Keys.NOTIF_ID_KEY, 1);
        String title = context.getString(R.string.notification_header);
        String content = intent.hasExtra(Constants.Keys.NOTIF_CONTENT_KEY) ? intent.getStringExtra(Constants.Keys.NOTIF_CONTENT_KEY) : "";

        Log.i(Constants.Tags.ALARM_TAG, "Alarm received in normal mode.");

        NotificationUtil notificationUtil = new NotificationUtil(context);
        notificationUtil.makeNotification(title, content, notifID);
    }

}
