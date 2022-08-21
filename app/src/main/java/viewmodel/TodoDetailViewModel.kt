package viewmodel

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hlv.cute.todo.R
import ir.hamsaa.persiandatepicker.date.PersianDateImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.DateTime
import model.Todo
import repo.dbRepoController.TodoDBRepository
import utils.DateHelper
import utils.ResourceProvider
import javax.inject.Inject

@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val provideResource: ResourceProvider,
    private val todoRepo: TodoDBRepository
) : ViewModel() {

    private val _todoStateFlow: MutableStateFlow<Todo?> = MutableStateFlow(null)
    val todoStateFlow: StateFlow<Todo?> = _todoStateFlow

    var todo: Todo?
        get() = todoStateFlow.value
        set(value) {
            _todoStateFlow.value = value
        }


    val doneText: String
        get() = if (todo!!.isDone) getString(R.string.is_done) else getString(R.string.is_undone)

    val imgDoneResource: Int
        get() = if (todo!!.isDone) R.drawable.checked_normal else R.drawable.unchecked_error

    val completeCreatedAt: String
        get() = getCompleteDate(todo!!.createdAt)

    val completeUpdatedAt: String
        get() = getCompleteDate(todo!!.updatedAt)

    fun getLytDateVisibility(): Int {
        return if (hasArriveDate()) View.VISIBLE else View.GONE
    }

    fun getLytCategoryVisibility(): Int {
        return if (hasCategory()) View.VISIBLE else View.GONE
    }

    fun getCreatedAtVisibility(): Int {
        return if (hasCreatedAt()) View.VISIBLE else View.GONE
    }

    fun getUpdatedAtVisibility(): Int {
        return if (hasUpdatedAt()) View.VISIBLE else View.GONE
    }

    fun hasArriveDate(): Boolean {
        return todo!!.arriveDate != 0L
    }

    fun hasCategory(): Boolean {
        return todo!!.categoryId != 0 && todo!!.category != null
    }

    fun hasCreatedAt(): Boolean {
        return todo!!.createdAt != 0L
    }

    fun hasUpdatedAt(): Boolean {
        return todo!!.updatedAt != 0L
    }

    private fun getDate(timeMillis: Long): DateTime {
        val dateTime = DateTime()
        val dateHelper = DateHelper(timeMillis)
        dateTime.hour = dateHelper.getHour()
        dateTime.minute = dateHelper.getMinute()
        val persianDate = PersianDateImpl()
        persianDate.setDate(timeMillis)
        dateTime.date = persianDate
        return dateTime
    }

    fun getString(@StringRes stringRes: Int): String {
        return provideResource.getString(stringRes)
    }

    private fun getCompleteDate(timeMillis: Long): String {
        val dateHelper = DateHelper(timeMillis)

        val hour = dateHelper.hourString
        val minute = dateHelper.minuteString

        val persianDate = PersianDateImpl().apply {
            setDate(timeMillis)
        }

        return persianDate.persianDayOfWeekName +
                "، " + persianDate.persianDay +
                " " + persianDate.persianMonthName +
                " " + persianDate.persianYear +
                "، ساعت " + hour + ":" + minute
    }

    fun getDateReminder(): String {
        return todo?.arriveDate?.let { getDate(it).persianDate } ?: ""
    }

    fun getClockReminder(): String {
        return todo?.arriveDate?.let { getDate(it).clock } ?: ""
    }

    fun refreshTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            var updatedTodo: Todo? = null

            launch {
                updatedTodo = todoRepo.getTodo(todo!!.id.toLong())
            }.join()

            updatedTodo?.let {
                todo = it
            }
        }
    }

}