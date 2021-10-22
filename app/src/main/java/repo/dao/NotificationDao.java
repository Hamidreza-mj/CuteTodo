package repo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import model.Notification;

@Dao
public interface NotificationDao {

    @Insert(entity = Notification.class, onConflict = OnConflictStrategy.REPLACE)
    void create(Notification notification);

    @Update(entity = Notification.class, onConflict = OnConflictStrategy.REPLACE)
    void update(Notification notification);

    @Delete(entity = Notification.class)
    void delete(Notification notification);


    @Query("SELECT * FROM notifications;")
    List<Notification> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE is_done = 1;")
    List<Notification> getAllDoneNotifications();

    @Query("SELECT * FROM notifications WHERE id = :id;")
    Notification getNotification(int id);

    @Query("DELETE FROM notifications;")
    void deleteAllNotifications();

    @Query("DELETE FROM notifications WHERE is_done = 1;")
    void deleteAllDoneNotifications();

    @Query("SELECT COUNT(*) FROM notifications;")
    long getNotificationsCount();
}
