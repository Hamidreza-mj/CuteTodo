package scheduler.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import hlv.cute.todo.R;
import model.Notification;
import repo.dbRepoController.NotificationDBRepository;
import utils.Constants;
import utils.NotificationUtil;

/**
 * this class is default receiver to handle Notification in normal mode
 */
public class NotificationReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.Tags.ALARM_TAG, "Alarm received in normal mode.");

        int notifID = intent.getIntExtra(Constants.Keys.NOTIF_ID_KEY, 0);
        if (notifID == 0)
            return;

        NotificationDBRepository repository = new NotificationDBRepository();

        Notification notification = null;
        try {
            notification = repository.getNotification(notifID);
        } catch (InterruptedException ignored) {
        }

        if (notification != null) {
            String title = context.getString(R.string.notification_header);
            String content = notification.getContent();

            if (content != null && content.trim().length() > 20)
                content = content.substring(0, 20).trim() + context.getString(R.string.ellipsis);


            NotificationUtil notificationUtil = new NotificationUtil(context);
            notificationUtil.makeNotification(title, context.getString(R.string.notification_content, content), notifID);

            Log.i(Constants.Tags.SUCCESS_ALARM, "Alarm received in normal mode successfully.");
        }

//        String content = intent.hasExtra(Constants.Keys.NOTIF_CONTENT_KEY) ? intent.getStringExtra(Constants.Keys.NOTIF_CONTENT_KEY) : "";
    }

}
