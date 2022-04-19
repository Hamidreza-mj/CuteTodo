package hlv.cute.todo.metrica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yandex.metrica.push.YandexMetricaPush;

import utils.Constants;

public class SilentPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract push message payload from your push message. (only in silent notification)
        // except in normal notification must using MainActivity get action intent (YandexMetricaPush.OPEN_DEFAULT_ACTIVITY_ACTION)
        String payload = intent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD);
        Log.e(Constants.Tags.DEBUG, "onReceive: " + payload);
    }
}
