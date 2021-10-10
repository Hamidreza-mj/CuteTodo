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
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import hlv.cute.todo.R;
import ui.activity.MainActivity;

public class NotificationUtil {

    private final Context context;

    private static final String CHANNEL_NAME = "cute-todo-channel-id";
    private String channelID = "CHANNEL_ID_";
    private Uri defaultSound;

    public NotificationUtil(Context context) {
        this.context = context;
    }

    private void createNotificationChannel() {
        channelID = "CHANNEL_ID_" + System.currentTimeMillis() / 1000;
        defaultSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getApplicationContext().getPackageName() + "/" + R.raw.notification);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            channel.setDescription("Cute Todo Channel Desc.");
            channel.enableVibration(true);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT) //immediately communication like sms, chat,...
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            channel.setSound(defaultSound, attributes);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public void makeNotification(String title, String message, int notificationID) {
        createNotificationChannel();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);

        Notification notification = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSound(defaultSound)
                .build();

        NotificationManagerCompat.from(context).notify(notificationID, notification);
    }
}
