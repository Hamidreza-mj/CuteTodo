package hlv.cute.todo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

import repo.database.TodoDatabase;
import scheduler.receiver.BootCompleteReceiver;
import utils.Constants;

public class App extends Application {

    @SuppressLint("StaticFieldLeak")
    private static App app;
    public Context applicationContext;
    private TodoDatabase todoDatabase;

    public static App get() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activateAppMetrica();
        activateFirebase();

        applicationContext = getApplicationContext();
        app = this;

        todoDatabase = TodoDatabase.get(applicationContext);

        //when boot
        ComponentName receiver = new ComponentName(getApplicationContext(), BootCompleteReceiver.class);
        getApplicationContext().getPackageManager().setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );

      /*  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
                <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
        }*/


    }

    private void activateAppMetrica() {
        YandexMetricaConfig appMetricaConfig = YandexMetricaConfig.newConfigBuilder(BuildConfig.METRICA_KEY)
//                .handleFirstActivationAsUpdate(isFirstActivationAsUpdate())
                .withStatisticsSending(true)
                .withLocationTracking(false)
                .withNativeCrashReporting(false)
                .withLogs()
                .build();
        YandexMetrica.activate(getApplicationContext(), appMetricaConfig);

        YandexMetrica.enableActivityAutoTracking(this);

        //handle push
        YandexMetricaPush.init(getApplicationContext());
    }

    private void activateFirebase() {
        FirebaseApp.initializeApp(this);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(Constants.Tags.FCM, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(Constants.Tags.FCM, "token ===> " + token);
                });

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Installations", "Installation ID: " + task.getResult());
                        String fid = task.getResult();
                        Log.d(Constants.Tags.FCM, "fid ===> " + fid);
                    } else {
                        Log.e("Installations", "Unable to get Installation ID");
                    }
                });
    }

    public TodoDatabase todoDatabase() {
        return todoDatabase;
    }

    private boolean isFirstActivationAsUpdate() {
        // Implement logic to detect whether the app is opening for the first time.
        // For example, you can check for files (settings, databases, and so on),
        // which the app creates on its first launch.
        // TODO: 4/18/22 first launch
        return true;
    }
}
