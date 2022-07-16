package scheduler.receiver

import android.content.BroadcastReceiver
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import scheduler.receiver.ReceiverController

/**
 * this class is receiver to rehandle Notification after boot up device
 */
class BootCompleteReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val controller = ReceiverController(context, intent)
        controller.handle()
    }
}