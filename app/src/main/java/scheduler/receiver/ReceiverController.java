package scheduler.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import hlv.cute.todo.R;
import model.Notification;
import repo.dbRepoController.NotificationDBRepository;
import scheduler.alarm.AlarmUtil;
import utils.Constants;
import utils.NotificationUtil;

public class ReceiverController {

    private final Context context;
    private final Intent intent;

    private boolean mustBeSetAgain = false;

    public ReceiverController(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    public void handle() {
        if (intent == null)
            return;

        String received = "Alarm received in normal mode.";
        String succeed = "Normal mode notification send successfully.";

        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            received = "Alarm received after boot completed.";
            succeed = "BootComplete mode notification send successfully.";
            mustBeSetAgain = true;
        }

        if (Objects.equals(intent.getAction(), "android.intent.action.TIME_SET")) {
            received = "Alarm received after time & date changed.";
            succeed = "Date Time mode notification send successfully.";
            mustBeSetAgain = true;
        }

        normalLog(received);

        NotificationDBRepository repository = new NotificationDBRepository();

        if (mustBeSetAgain) {
            try {
                List<Notification> notifications = repository.getAllNotifications();
                if (notifications != null) {
                    for (Notification notification : notifications) {
                        //AlarmUtil.with(context.getApplicationContext()).cancelAlarm(notification.getId());
                        AlarmUtil.with(context.getApplicationContext()).setAlarm(notification.getId(), notification.getArriveDate());
                    }
                }
            } catch (InterruptedException ignored) {
            }

            mustBeSetAgain = false;
        }

        int notifID = intent.getIntExtra(Constants.Keys.NOTIF_ID_KEY, 0);
        if (notifID == 0)
            return;


        Notification notification = null;
        try {
            notification = repository.getNotification(notifID);
        } catch (InterruptedException ignored) {
        }

        if (notification != null) {
            String title = context.getString(R.string.notification_header);
            String content = notification.getContent();
            String summary = content;

            if (content != null && content.trim().length() > 100)
                content = content.substring(0, 100).trim() + context.getString(R.string.ellipsis);


            NotificationUtil notificationUtil = new NotificationUtil(context);
            notificationUtil.makeNotification(
                    title,
                    summary,
                    context.getString(R.string.notification_content, content),
                    notification.getPriority(),
                    notifID
            );

            successLog(succeed);
        }
    }

    private void successLog(String log) {
        Log.i(Constants.Tags.SUCCESS_NOTIF_ALARM, log);
    }

    private void normalLog(String log) {
        Log.i(Constants.Tags.ALARM_TAG, log);
    }

}
