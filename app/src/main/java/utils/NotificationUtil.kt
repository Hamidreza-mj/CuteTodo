package utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import hlv.cute.todo.R
import model.Priority
import ui.activity.ShowNotificationActivity
import javax.inject.Inject

class NotificationUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val provideResource: ResourceProvider
) {

    private val defaultSound: Uri =
        Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.applicationContext.packageName + "/" + R.raw.notification)

    private val soundAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT) //immediately communication like sms, chat,...
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    companion object {
        private const val CHANNEL_ID = "cute-todo-channel-id"
        private const val CHANNEL_NAME = "Todo Reminder"
    }

    fun makeNotification(
        title: String?,
        summary: String?,
        content: String?,
        priority: Priority?,
        notificationID: Int
    ) {
        createNotificationChannel()

        val intent = Intent(context, ShowNotificationActivity::class.java).apply {
            //use with android:taskAffinity="ui.activity.ShowNotificationActivity"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Constants.Keys.NOTIF_ID_DETAIL, notificationID)
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                notificationID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


        val priorityColor = when (priority) {
            Priority.LOW -> provideResource.getColor(R.color.blue)

            Priority.NORMAL -> provideResource.getColor(R.color.orange)

            Priority.HIGH -> provideResource.getColor(R.color.red)

            else -> provideResource.getColor(R.color.blue)
        }


        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.notification_logo)

        val notification = NotificationCompat.Builder(
            context, CHANNEL_ID
        )
            .setSmallIcon(R.drawable.notification_logo)
            .setAutoCancel(true)
            .setLargeIcon(icon)
            .setContentTitle(title)
            .setContentText(summary)
            .setColor(priorityColor)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) //.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setSound(defaultSound, AudioManager.STREAM_NOTIFICATION)
            .addAction(R.drawable.ic_detail, provideResource.getString(R.string.open_notif_action), pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Cute Todo Notification Channel"
                    enableVibration(true)
                    setSound(defaultSound, soundAttributes)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    importance = NotificationManager.IMPORTANCE_HIGH
                }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

}