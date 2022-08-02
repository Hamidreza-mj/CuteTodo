package scheduler.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * this class is default receiver to handle Notification in normal mode
 */
@AndroidEntryPoint
class NotificationReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var receiverController: ReceiverController

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        receiverController.handleWith(intent)
    }
}