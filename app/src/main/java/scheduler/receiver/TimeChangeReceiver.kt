package scheduler.receiver

import android.content.BroadcastReceiver
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import scheduler.receiver.ReceiverController

/**
 * this class is receiver to rehandle Notification after date and time of deivce changed
 */
class TimeChangeReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val controller = ReceiverController(context, intent)
        controller.handle()
    }
}