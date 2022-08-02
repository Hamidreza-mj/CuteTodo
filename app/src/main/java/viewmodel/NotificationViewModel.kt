package viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import model.Notification
import model.Todo
import repo.dbRepoController.NotificationDBRepository
import scheduler.alarm.AlarmUtil
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val dbRepository: NotificationDBRepository,
    private val alarmUtil: AlarmUtil
) : ViewModel() {


    fun addNotification(todo: Todo) {
        try {
            if (todo.arriveDate < System.currentTimeMillis()) return

            val notification = Notification().apply {
                initWith(todo)
            }

            dbRepository.addNotification(notification)
            alarmUtil.setAlarm(todo.id, todo.arriveDate)

        } catch (ignored: InterruptedException) {
        }
    }

    private fun editNotification(todo: Todo) {
        try {
            //cancel and delete old alarm
            val notification = Notification().apply {
                initWith(todo)
            }

            alarmUtil.cancelAlarm(notification.id)

            if (todo.arriveDate < System.currentTimeMillis()) {
                deleteNotification(notification)
                return
            }

            dbRepository.editNotification(notification)

            alarmUtil.setAlarm(todo.id, todo.arriveDate)
        } catch (ignored: InterruptedException) {
        }
    }

    private fun deleteNotification(notification: Notification) {
        try {
            dbRepository.deleteNotification(notification)
        } catch (ignored: InterruptedException) {
        }
    }

    private fun getAllNotifications(): List<Notification>? {
        return try {
            dbRepository.getAllNotifications()
        } catch (e: InterruptedException) {
            null
        }
    }

    private fun getAllDoneNotifications(): List<Notification>? {
        return try {
            dbRepository.getAllDoneNotifications()
        } catch (e: InterruptedException) {
            null
        }
    }

    private fun getNotification(id: Int): Notification? {
        return try {
            dbRepository.getNotification(id.toLong())
        } catch (e: InterruptedException) {
            null
        }
    }

    private fun existsNotification(id: Int): Boolean {
        return getNotification(id) != null
    }

    fun setNotificationEditMode(todo: Todo) {
        if (existsNotification(todo.id))
            editNotification(todo)
        else
            addNotification(todo)
    }

    private fun deleteAllNotifications() {
        try {
            dbRepository.deleteAllNotifications()
        } catch (ignored: InterruptedException) {
        }
    }

    private fun deleteAllDoneNotifications() {
        try {
            dbRepository.deleteAllDoneNotifications()
        } catch (ignored: InterruptedException) {
        }
    }

    fun cancelAllDoneAlarm() {
        val notificationList = getAllDoneNotifications()

        if (notificationList != null) {
            for (notif: Notification in notificationList)
                alarmUtil.cancelAlarm(notif.id)

        }

        deleteAllDoneNotifications()
    }

    fun cancelAllAlarm() {
        val notificationList = getAllNotifications()
        if (notificationList != null) {
            for (notif: Notification in notificationList)
                alarmUtil.cancelAlarm(notif.id)
        }

        deleteAllNotifications()
    }

    fun cancelAlarm(todo: Todo?) {
        val notification = Notification()
        notification.initWith(todo)
        alarmUtil.cancelAlarm(notification.id)

        deleteNotification(notification)
    }

}