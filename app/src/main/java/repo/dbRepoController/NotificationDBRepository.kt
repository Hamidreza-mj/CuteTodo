package repo.dbRepoController

import model.Notification
import repo.dao.NotificationDao
import javax.inject.Inject

class NotificationDBRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {

    suspend fun getAllNotifications(): List<Notification>? {
        return notificationDao.getAllNotifications()
    }

    suspend fun getAllDoneNotifications(): List<Notification>? {
        return notificationDao.getAllDoneNotifications()
    }

    suspend fun getNotification(id: Long): Notification? {
        return notificationDao.getNotification(id)
    }

    suspend fun addNotification(notification: Notification?) {
        notificationDao.create(notification)
    }

    suspend fun editNotification(notification: Notification?) {
        notificationDao.update(notification)
    }

    suspend fun deleteNotification(notification: Notification?) {
        notificationDao.delete(notification)
    }

    suspend fun deleteShownNotifications() {
        notificationDao.deleteShownNotifications()
    }

    suspend fun deleteAllNotifications() {
        notificationDao.deleteAllNotifications()
    }

    suspend fun deleteAllDoneNotifications() {
        notificationDao.deleteAllDoneNotifications()
    }

    suspend fun setDoneTodo(id: Long) {
        notificationDao.setDoneTodo(id)
    }

    suspend fun setShownTodo(id: Long) {
        notificationDao.setShownTodo(id)
    }

    @Throws(InterruptedException::class)
    fun getAllNotificationsWithinThread(): List<Notification>? {
        var notificationList: List<Notification>? = null

        Thread {
            notificationList = notificationDao.getAllNotificationsThread()
        }.apply {
            start()
            join()
        }

        return notificationList
    }

    @Throws(InterruptedException::class)
    fun getNotificationWithinThread(id: Long): Notification? {
        var notification: Notification? = null

        Thread {
            notification = notificationDao.getNotificationThread(id)
        }.apply {
            start()
            join()
        }

        return notification
    }

}