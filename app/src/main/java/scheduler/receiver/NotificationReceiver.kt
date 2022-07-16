package scheduler.receiver

import android.content.BroadcastReceiver
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import scheduler.receiver.ReceiverController

/**
 * this class is default receiver to handle Notification in normal mode
 */
class NotificationReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val controller = ReceiverController(context, intent)
        controller.handle()
    }
}