package repo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import model.Category;
import model.Notification;
import model.Todo;
import repo.dao.CategoryDao;
import repo.dao.NotificationDao;
import repo.dao.TodoDao;

@Database(entities = {Todo.class, Category.class, Notification.class}, version = 1, exportSchema = false)
public abstract class TodoDatabase extends RoomDatabase {

    private static final String DB_NAME = "TodoDB";
    private static TodoDatabase instance;

    public abstract TodoDao getTodoDao();

    public abstract CategoryDao getCategoryDao();

    public abstract NotificationDao getNotificationDao();

    public static synchronized TodoDatabase get(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TodoDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    //.addCallback(roomCallBack)
                    //.allowMainThreadQueries() this allow room to run db process in main thread (UI Thread)
                    //it is recommended to be disallowed
                    .build();
        }
        return instance;
    }

    /*private static final RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };*/

}
