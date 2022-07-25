package utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import hlv.cute.todo.R;
import model.Priority;
import ui.activity.ShowNotificationActivity;

public class NotificationUtil {

    private final Context context;

    private static final String CHANNEL_ID = "cute-todo-channel-id";
    private static final String CHANNEL_NAME = "Todo Reminder";
    private final Uri defaultSound;
    private final AudioAttributes soundAttributes;

    public NotificationUtil(Context context) {
        this.context = context;
        defaultSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getApplicationContext().getPackageName() + "/" + R.raw.notification);
        soundAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT) //immediately communication like sms, chat,...
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Cute Todo Notification Channel");
            channel.enableVibration(true);
            channel.setSound(defaultSound, soundAttributes);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public void makeNotification(String title, String summary, String content, Priority priority, int notificationID) {
        createNotificationChannel();

        Intent intent = new Intent(context, ShowNotificationActivity.class);
        //use with android:taskAffinity="ui.activity.ShowNotificationActivity"
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.Keys.NOTIF_ID_DETAIL, notificationID);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int priorityColor = ContextCompat.getColor(context, R.color.blue);
        if (priority != null) {
            switch (priority) {
                case LOW:
                default:
                    priorityColor = ContextCompat.getColor(context, R.color.blue);
                    break;

                case NORMAL:
                    priorityColor = ContextCompat.getColor(context, R.color.orange);
                    break;

                case HIGH:
                    priorityColor = ContextCompat.getColor(context, R.color.red);
                    break;
            }
        }

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_logo);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_logo)
                .setAutoCancel(true)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(summary)
                .setColor(priorityColor)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSound(defaultSound, AudioManager.STREAM_NOTIFICATION)
                .build();

        NotificationManagerCompat.from(context).notify(notificationID, notification);
    }
}
