package hlv.cute.todo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import repo.database.TodoDatabase;

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
    }

    public TodoDatabase todoDatabase() {
        return todoDatabase;
    }
}
