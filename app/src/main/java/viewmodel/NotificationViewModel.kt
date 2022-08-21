package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private suspend fun getAllNotifications(): List<Notification>? {
        return dbRepository.getAllNotifications()
    }

    private suspend fun getAllDoneNotifications(): List<Notification>? {
        return dbRepository.getAllDoneNotifications()
    }

    fun addNotification(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (todo.arriveDate < System.currentTimeMillis())
                return@launch

            val notification = Notification().apply {
                initWith(todo)
            }

            launch {
                dbRepository.addNotification(notification)
            }.join()

            alarmUtil.setAlarm(todo.id, todo.arriveDate)
        }
    }

    private fun editNotification(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            //cancel and delete old alarm
            val notification = Notification().apply {
                initWith(todo)
            }

            alarmUtil.cancelAlarm(notification.id)

            if (todo.arriveDate < System.currentTimeMillis()) {
                launch {
                    dbRepository.deleteNotification(notification)
                }.join()

                return@launch
            }

            launch {
                dbRepository.editNotification(notification)
            }.join()

            alarmUtil.setAlarm(todo.id, todo.arriveDate)
        }
    }

    private suspend fun existsNotification(notificationId: Int): Boolean {
        var notif: Notification? = null

        viewModelScope.launch(Dispatchers.IO) {
            notif = dbRepository.getNotification(notificationId.toLong())
        }.join()

        return notif != null
    }

    fun setNotificationEditMode(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (existsNotification(todo.id))
                editNotification(todo)
            else
                addNotification(todo)
        }
    }

    fun cancelAllDoneAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            var notificationList: List<Notification>?

            launch {
                notificationList = getAllDoneNotifications()

                if (notificationList != null) {
                    for (notif: Notification in notificationList!!)
                        alarmUtil.cancelAlarm(notif.id)
                }
            }.join()

            dbRepository.deleteAllDoneNotifications()
        }
    }

    fun cancelAllAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            var notificationList: List<Notification>?

            launch {
                notificationList = getAllNotifications()

                if (notificationList != null) {
                    for (notif: Notification in notificationList!!)
                        alarmUtil.cancelAlarm(notif.id)
                }
            }.join()

            dbRepository.deleteAllNotifications()
        }
    }

    fun cancelAlarm(todo: Todo?) {
        val notification = Notification().apply {
            initWith(todo)
        }

        alarmUtil.cancelAlarm(notification.id)

        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.deleteNotification(notification)
        }
    }

}