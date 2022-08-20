package repo.dao

import androidx.room.*
import model.Notification

@Dao
interface NotificationDao {

    @Insert(entity = Notification::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(notification: Notification?)

    @Update(entity = Notification::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(notification: Notification?)

    @Delete(entity = Notification::class)
    suspend fun delete(notification: Notification?)

    @Query("SELECT * FROM notifications;")
    suspend fun getAllNotifications(): List<Notification>?

    @Query("SELECT * FROM notifications WHERE is_done = 1;")
    suspend fun getAllDoneNotifications(): List<Notification>?

    @Query("DELETE FROM notifications WHERE is_shown = 1;")
    suspend fun deleteShownNotifications()

    @Query("SELECT * FROM notifications WHERE id = :id;")
    suspend fun getNotification(id: Long): Notification?

    @Query("UPDATE notifications SET is_done = not is_done WHERE id = :id")
    suspend fun setDoneTodo(id: Long)

    @Query("UPDATE notifications SET is_shown = 1 WHERE id = :id;")
    suspend fun setShownTodo(id: Long)

    @Query("DELETE FROM notifications;")
    suspend fun deleteAllNotifications()

    @Query("DELETE FROM notifications WHERE is_done = 1;")
    suspend fun deleteAllDoneNotifications()

    @Query("SELECT * FROM notifications WHERE id = :id;")
    fun getNotificationThread(id: Long): Notification?

    @Query("SELECT * FROM notifications;")
    fun getAllNotificationsThread(): List<Notification>?
}