package repo.dbRepoController

import model.Notification
import repo.dao.NotificationDao
import javax.inject.Inject

class NotificationDBRepository @Inject constructor(
    private val notificationDao: NotificationDao
) {

    @Throws(InterruptedException::class)
    fun addNotification(notification: Notification?) {
        Thread {
            notificationDao.create(notification)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun editNotification(notification: Notification?) {
        Thread {
            notificationDao.update(notification)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun deleteNotification(notification: Notification?) {
        Thread {
            notificationDao.delete(notification)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun getAllNotifications(): List<Notification>? {
        var notificationList: List<Notification>? = null

        Thread {
            notificationList = notificationDao.getAllNotifications()
        }.apply {
            start()
            join()
        }

        return notificationList
    }

    @Throws(InterruptedException::class)
    fun deleteShownNotifications() {
        Thread {
            notificationDao.deleteShownNotifications()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun getAllDoneNotifications(): List<Notification>? {
        var notificationDoneList: List<Notification>? = null
        Thread {
            notificationDoneList = notificationDao.getAllDoneNotifications()
        }.apply {
            start()
            join()
        }

        return notificationDoneList
    }

    @Throws(InterruptedException::class)
    fun deleteAllNotifications() {
        Thread {
            notificationDao.deleteAllNotifications()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun deleteAllDoneNotifications() {
        Thread {
            notificationDao.deleteAllDoneNotifications()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun getNotification(id: Long): Notification? {
        var notification: Notification? = null

        Thread {
            notification = notificationDao.getNotification(id)
        }.apply {
            start()
            join()
        }

        return notification
    }

    @Throws(InterruptedException::class)
    fun setDoneTodo(id: Long) {
        Thread {
            notificationDao.setDoneTodo(id)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun setShownTodo(id: Long) {
        Thread {
            notificationDao.setShownTodo(id)
        }.apply {
            start()
            join()
        }
    }
}