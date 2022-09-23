package hlv.cute.todo

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.FirebaseApp
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.push.YandexMetricaPush
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import scheduler.receiver.BootCompleteReceiver
import utils.Constants
import utils.NotificationUtil
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    private var scope: CoroutineScope? = MainScope()

    private val Context.dataStoreCutePref: DataStore<Preferences> by preferencesDataStore(name = Constants.Names.CUTE_PREF_NAME)
    private val firstRunPrefKey = booleanPreferencesKey(Constants.Keys.FIRST_RUN_V1)

    @Inject
    lateinit var notificationUtil: NotificationUtil

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

        //if channel not exists auto create it
        notificationUtil.createNotificationChannel()

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
        scope?.launch {
            val appMetricaConfig =
                YandexMetricaConfig.newConfigBuilder(BuildConfig.METRICA_KEY)
                    .handleFirstActivationAsUpdate(isFirstActivationAsUpdateOrInstall())
                    .withStatisticsSending(true)
                    .withLocationTracking(false)
                    .withNativeCrashReporting(false)
                    .withLogs()
                    .build()
            YandexMetrica.activate(applicationContext, appMetricaConfig)
            YandexMetrica.enableActivityAutoTracking(this@App)

            //handle push
            YandexMetricaPush.init(applicationContext)
        }
    }

    private fun activateFirebase() {
        FirebaseApp.initializeApp(this)

        /*FirebaseMessaging.getInstance().token
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
            }*/
    }

    /**
     * Implement logic to detect whether the app is opening for the first time.
     * For example, you can check for files (settings, databases, and so on),
     * which the app creates on its first launch.
     */
    private suspend fun isFirstActivationAsUpdateOrInstall(): Boolean {
        var isFirstRun: Boolean? = applicationContext.dataStoreCutePref.data
            .catch {
                Log.e(Constants.Tags.DEBUG, "exception occured in first run!")
            }
            .first()
            .toPreferences()[firstRunPrefKey]

        when (isFirstRun) {
            null -> {
                //is first run, it has not been handled before!
                applicationContext.dataStoreCutePref.edit { preferences ->
                    preferences[firstRunPrefKey] = true
                    isFirstRun = true
                }

                return isFirstRun ?: true
            }

            true -> {
                applicationContext.dataStoreCutePref.edit { preferences ->
                    preferences[firstRunPrefKey] = false
                    isFirstRun = false
                }

                return isFirstRun ?: false
            }

            false -> {
                return false
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        scope?.cancel()
        scope = MainScope()
    }
}