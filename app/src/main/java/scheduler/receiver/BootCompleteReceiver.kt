package scheduler.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * this class is receiver to rehandle Notification after boot up device
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        ReceiverController controller = new ReceiverController(context, intent);
        controller.handle();

    }

}
