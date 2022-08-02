package scheduler.receiver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * this class is receiver to rehandle Notification after date and time of deivce changed
 */
@AndroidEntryPoint
class TimeChangeReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var receiverController: ReceiverController

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        receiverController.handleWith(intent)
    }
}