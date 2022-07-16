package repo.dao

import androidx.room.*
import model.Notification

@Dao
interface NotificationDao {

    @Insert(entity = Notification::class, onConflict = OnConflictStrategy.REPLACE)
    fun create(notification: Notification?)

    @Update(entity = Notification::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(notification: Notification?)

    @Delete(entity = Notification::class)
    fun delete(notification: Notification?)

    @Query("SELECT * FROM notifications;")
    fun getAllNotifications(): List<Notification>?

    @Query("SELECT * FROM notifications WHERE is_done = 1;")
    fun getAllDoneNotifications(): List<Notification>?

    @Query("DELETE FROM notifications WHERE is_shown = 1;")
    fun deleteShownNotifications()

    @Query("SELECT * FROM notifications WHERE id = :id;")
    fun getNotification(id: Long): Notification?

    @Query("UPDATE notifications SET is_done = not is_done WHERE id = :id")
    fun setDoneTodo(id: Long)

    @Query("UPDATE notifications SET is_shown = 1 WHERE id = :id;")
    fun setShownTodo(id: Long)

    @Query("DELETE FROM notifications;")
    fun deleteAllNotifications()

    @Query("DELETE FROM notifications WHERE is_done = 1;")
    fun deleteAllDoneNotifications()
}