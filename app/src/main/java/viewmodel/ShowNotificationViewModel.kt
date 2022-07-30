package viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.hamsaa.persiandatepicker.date.PersianDateImpl
import model.DateTime
import model.Notification
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

    private val _notificationLiveData: MutableLiveData<Notification> = MutableLiveData()
    private val _closeLive: MutableLiveData<Boolean> = MutableLiveData()
    private val _runMainLive: MutableLiveData<Boolean> = MutableLiveData()

    val notificationLiveData: LiveData<Notification> = _notificationLiveData
    val closeLive: LiveData<Boolean> = _closeLive
    val runMainLive: LiveData<Boolean> = _runMainLive

    val notification: Notification?
        get() = notificationLiveData.value

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
        if (!intent.hasExtra(Constants.Keys.NOTIF_ID_DETAIL)) {
            mustClose()
            return
        }

        val notifId = intent.getIntExtra(Constants.Keys.NOTIF_ID_DETAIL, 0)
        if (notifId == 0) {
            mustClose()
            return
        }

        var notification: Notification? = null
        try {
            notification = dbRepository.getNotification(notifId.toLong())
        } catch (ignored: InterruptedException) {
        }

        if (notification == null) {
            try {
                val todo = todoRepository.getTodo(notifId.toLong())
                if (todo != null) {
                    val newNotification = Notification().apply {
                        initWith(todo)
                    }

                    _notificationLiveData.value = newNotification
                    return
                }
            } catch (ignored: InterruptedException) {
            }

            _runMainLive.value = true //run main if notification or todo == null
            return
        }

        _notificationLiveData.value = notification
    }

    fun deleteNotification() {
        if (notification == null) return
        try {
            dbRepository.deleteNotification(notification)
        } catch (ignored: InterruptedException) {
        }
    }

    fun done() {
        if (notification == null) return
        try {
            todoRepository.setTodoIsDone(notification!!.id.toLong())
        } catch (ignored: InterruptedException) {
        }
    }

    fun setShown() {
        if (notification == null) return
        try {
            dbRepository.setShownTodo(notification!!.id.toLong())
        } catch (ignored: InterruptedException) {
        }
    }


    private fun mustClose() {
        _closeLive.value = true
    }

}