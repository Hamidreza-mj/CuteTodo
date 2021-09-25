package repo.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import model.Todo;
import repo.dao.TodoDao;

@Database(entities = {Todo.class}, version = 1)
public abstract class TodoDatabase extends RoomDatabase {

    private static final String DB_NAME = "TodoDB";
    private static TodoDatabase instance;

    public abstract TodoDao getTodoDao();

    public static synchronized TodoDatabase get(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TodoDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    //.allowMainThreadQueries() this allow room to run db process in main thread (UI Thread)
                    //it is recommended to be disallowed
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            //new ClearDB(instance).execute();
        }
    };

    static class ClearDB extends AsyncTask<Void, Void, Integer> {
        private final TodoDao todoDao;

        public ClearDB(TodoDatabase todoDatabase) {
            todoDao = todoDatabase.getTodoDao();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return todoDao.deleteAllTodos();
        }
    }

}
