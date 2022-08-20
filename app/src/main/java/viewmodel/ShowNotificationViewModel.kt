package viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.hamsaa.persiandatepicker.date.PersianDateImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import model.DateTime
import model.Notification
import model.Todo
import repo.dbRepoController.NotificationDBRepository
import repo.dbRepoController.TodoDBRepository
import utils.Constants
import utils.DateHelper
import javax.inject.Inject

@HiltViewModel
class ShowNotificationViewModel @Inject constructor(
    private val dbRepository: NotificationDBRepository,
    private val todoRepository: TodoDBRepository
) : ViewModel() {

    private val _notificationStateFlow: MutableStateFlow<Notification?> = MutableStateFlow(null)
    val notificationStateFlow: StateFlow<Notification?> = _notificationStateFlow.asStateFlow()


    //--------------channels----------------
    private val _closeChannel: Channel<Boolean> = Channel()
    val closeFlow: Flow<Boolean> = _closeChannel.receiveAsFlow()

    private val _runMainChannel: Channel<Boolean> = Channel()
    val runMainFlow: Flow<Boolean> = _runMainChannel.receiveAsFlow()


    val notification: Notification?
        get() = _notificationStateFlow.value

    fun setIntent(intent: Intent) = handleExtras(intent)

    fun getLytDateVisibility(): Int {
        return if (hasArriveDate())
            View.VISIBLE
        else
            View.GONE
    }

    fun getLytCategoryVisibility(): Int {
        return if (hasCategory())
            View.VISIBLE
        else
            View.GONE
    }

    fun hasArriveDate(): Boolean {
        return if (notification == null)
            false
        else
            notification!!.arriveDate != 0L
    }

    fun hasCategory(): Boolean {
        return if (notification == null)
            false
        else
            notification!!.categoryId != 0 && notification!!.category != null
    }

    fun getDateReminder(): String {
        return if (notification == null)
            ""
        else
            getDate(notification!!.arriveDate).persianDate
    }

    fun getClockReminder(): String {
        return if (notification == null)
            ""
        else
            getDate(notification!!.arriveDate).clock
    }

    private fun getDate(timeMillis: Long): DateTime {
        val dateHelper = DateHelper(timeMillis)

        val dateTime = DateTime().apply {
            hour = dateHelper.getHour()
            minute = dateHelper.getMinute()
        }

        val persianDate = PersianDateImpl().apply {
            setDate(timeMillis)
        }

        dateTime.date = persianDate
        return dateTime
    }

    private fun handleExtras(intent: Intent) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!intent.hasExtra(Constants.Keys.NOTIF_ID_DETAIL)) {
                mustClose()
                return@launch
            }

            val notifId = intent.getIntExtra(Constants.Keys.NOTIF_ID_DETAIL, 0)
            if (notifId == 0) {
                mustClose()
                return@launch
            }

            var notification: Notification? = null
            launch {
                notification = dbRepository.getNotification(notifId.toLong())
            }.join()

            if (notification == null) {
                var todo: Todo? = null

                launch {
                    todo = todoRepository.getTodo(notifId.toLong())
                }.join()

                if (todo != null) {
                    val newNotification = Notification().apply {
                        initWith(todo)
                    }

                    _notificationStateFlow.value = newNotification
                    return@launch
                }


                _runMainChannel.send(true) //run main if notification or todo == null
                return@launch
            }

            _notificationStateFlow.value = notification
        }
    }

    fun deleteNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            if (notification == null) return@launch

            launch {
                dbRepository.deleteNotification(notification)
            }.join()
        }
    }

    fun done() {
        viewModelScope.launch(Dispatchers.IO) {
            if (notification == null) return@launch

            launch {
                todoRepository.setTodoIsDone(notification!!.id.toLong())
            }.join()
        }
    }

    fun setShown() {
        viewModelScope.launch(Dispatchers.IO) {
            if (notification == null) return@launch

            launch {
                dbRepository.setShownTodo(notification!!.id.toLong())
            }.join()
        }
    }

    private fun mustClose() {
        viewModelScope.launch {
            _closeChannel.send(true)
        }
    }

}