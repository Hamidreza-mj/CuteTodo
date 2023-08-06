package hlv.cute.todo

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.FirebaseApp
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import com.yandex.metrica.push.YandexMetricaPush
import dagger.hilt.android.HiltAndroidApp
import data.datastore.DataStoreManager
import data.datastore.PrefsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import scheduler.receiver.BootCompleteReceiver
import ui.util.AppThemeHandler
import utils.NotificationUtil
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    private var scope: CoroutineScope? = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Inject
    lateinit var notificationUtil: NotificationUtil

    @Inject
    lateinit var themeHandler: AppThemeHandler

    @Inject
    lateinit var prefsDataStore: PrefsDataStore

    override fun onCreate() {
        super.onCreate()

        runBlocking {
            val currentTheme = themeHandler.getCurrentTheme()
            /*AppThemeHandler.ThemeType.Dark*/
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

//            scope?.launch {
            themeHandler.applyNewTheme(
                themeType = currentTheme,
                onThemeChange = {
//                        activity?.let { activity ->
//                            val delegate =
//                                AppCompatDelegate.create(
//                                    activity,
//                                    null
//                                )
//                            delegate.applyDayNight()
//                        }
                }
            )
//            }
        }


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
        val firstRunKey: Preferences.Key<Boolean> = PrefsDataStore.PreferencesKeys.firstRunPrefKey

        val dataStore: DataStore<Preferences>
        prefsDataStore.apply {
            dataStore = applicationContext.appDataStore
        }

        val isFirstRun: Boolean? = DataStoreManager.readPreference(
            dataStore = dataStore,
            preferenceKey = firstRunKey,
            false
        ).firstOrNull()

        when (isFirstRun) {
            null -> {
                //is first run, it has not been handled before!
                DataStoreManager.writePreference(
                    dataStore = dataStore,
                    preferenceKey = firstRunKey,
                    value = true
                )

                return true
            }

            true -> {
                //not first run
                DataStoreManager.writePreference(
                    dataStore = dataStore,
                    preferenceKey = firstRunKey,
                    value = false
                )

                return false
            }

            false -> {
                //not first run
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