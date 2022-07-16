package repo.dbRepoController

import hlv.cute.todo.App
import model.Notification
import repo.dao.NotificationDao

class NotificationDBRepository {

    private val dao: NotificationDao = App.get()!!.todoDatabase()!!.notificationDao!!

    @Throws(InterruptedException::class)
    fun addNotification(notification: Notification?) {
        Thread {
            dao.create(notification)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun editNotification(notification: Notification?) {
        Thread {
            dao.update(notification)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun deleteNotification(notification: Notification?) {
        Thread {
            dao.delete(notification)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun getAllNotifications(): List<Notification>? {
        var notificationList: List<Notification>? = null

        Thread {
            notificationList = dao.getAllNotifications()
        }.apply {
            start()
            join()
        }

        return notificationList
    }

    @Throws(InterruptedException::class)
    fun deleteShownNotifications() {
        Thread {
            dao.deleteShownNotifications()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun getAllDoneNotifications(): List<Notification>? {
        var notificationDoneList: List<Notification>? = null
        Thread {
            notificationDoneList = dao.getAllDoneNotifications()
        }.apply {
            start()
            join()
        }

        return notificationDoneList
    }

    @Throws(InterruptedException::class)
    fun deleteAllNotifications() {
        Thread {
            dao.deleteAllNotifications()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun deleteAllDoneNotifications() {
        Thread {
            dao.deleteAllDoneNotifications()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun getNotification(id: Long): Notification? {
        var notification: Notification? = null

        Thread {
            notification = dao.getNotification(id)
        }.apply {
            start()
            join()
        }

        return notification
    }

    @Throws(InterruptedException::class)
    fun setDoneTodo(id: Long) {
        Thread {
            dao.setDoneTodo(id)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun setShownTodo(id: Long) {
        Thread {
            dao.setShownTodo(id)
        }.apply {
            start()
            join()
        }
    }
}