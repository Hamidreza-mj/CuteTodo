package repo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import model.Category
import model.Notification
import model.Todo
import repo.dao.CategoryDao
import repo.dao.NotificationDao
import repo.dao.TodoDao

@Database(
    entities = [Todo::class, Category::class, Notification::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {

    abstract val todoDao: TodoDao?
    abstract val categoryDao: CategoryDao?
    abstract val notificationDao: NotificationDao?

    companion object {
        private const val DB_NAME = "TodoDB"
        private var instance: TodoDatabase? = null

        @Synchronized
        operator fun get(context: Context): TodoDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    //.addCallback(roomCallBack)
                    //.allowMainThreadQueries() this allow room to run db process in main thread (UI Thread)
                    //it is recommended to be disallowed
                    .build()
            }
            return instance
        }

        /*private val roomCallBack: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }*/
    }
}