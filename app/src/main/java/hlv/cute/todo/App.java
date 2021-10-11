package hlv.cute.todo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import repo.database.TodoDatabase;
import scheduler.receiver.BootCompleteReceiver;

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

    public TodoDatabase todoDatabase() {
        return todoDatabase;
    }
}
