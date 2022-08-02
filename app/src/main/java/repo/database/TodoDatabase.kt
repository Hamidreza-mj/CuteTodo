package repo.database

import androidx.room.Database
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

    abstract val todoDao: TodoDao
    abstract val categoryDao: CategoryDao
    abstract val notificationDao: NotificationDao

}