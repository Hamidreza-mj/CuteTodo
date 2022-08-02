package hlv.cute.todo

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.push.YandexMetricaPush
import dagger.hilt.android.HiltAndroidApp
import scheduler.receiver.BootCompleteReceiver
import utils.Constants

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        activateAppMetrica()

        activateFirebase()

        //when boot
        val receiver = ComponentName(applicationContext, BootCompleteReceiver::class.java)
        applicationContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

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

    private fun activateAppMetrica() {
        val appMetricaConfig =
            YandexMetricaConfig.newConfigBuilder(BuildConfig.METRICA_KEY)
                //.handleFirstActivationAsUpdate(isFirstActivationAsUpdate())
                .withStatisticsSending(true)
                .withLocationTracking(false)
                .withNativeCrashReporting(false)
                .withLogs()
                .build()
        YandexMetrica.activate(applicationContext, appMetricaConfig)
        YandexMetrica.enableActivityAutoTracking(this)

        //handle push
        YandexMetricaPush.init(applicationContext)
    }

    private fun activateFirebase() {
        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    Log.w(
                        Constants.Tags.FCM,
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d(Constants.Tags.FCM, "token ===> $token")
            }
        FirebaseInstallations.getInstance().id
            .addOnCompleteListener { task: Task<String> ->
                if (task.isSuccessful) {
                    Log.d("Installations", "Installation ID: " + task.result)
                    val fid = task.result
                    Log.d(Constants.Tags.FCM, "fid ===> $fid")
                } else {
                    Log.e("Installations", "Unable to get Installation ID")
                }
            }
    }

    // Implement logic to detect whether the app is opening for the first time.
    // For example, you can check for files (settings, databases, and so on),
    // which the app creates on its first launch.
    // TODO: 4/18/22 first launch
    private val getAppContextisFirstActivationAsUpdate: Boolean
        get() =// Implement logic to detect whether the app is opening for the first time.
        // For example, you can check for files (settings, databases, and so on),
        // which the app creates on its first launch.
            // TODO: 4/18/22 first launch
            true
}