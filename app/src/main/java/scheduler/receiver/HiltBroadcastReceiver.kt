package scheduler.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.CallSuper

/**
 * https://stackoverflow.com/questions/62335727/hilt-injection-not-working-with-broadcastreceiver#answer-62433984
 *
 * this is wrapper BroadcastReceiver class for hilt
 * to fix call super method
 *
 */
abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context?, intent: Intent?) {
    }
}