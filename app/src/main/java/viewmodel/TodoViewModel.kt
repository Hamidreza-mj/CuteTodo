package viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import model.Filter
import model.Notification
import model.Todo
import repo.dbRepoController.NotificationDBRepository
import repo.dbRepoController.TodoDBRepository
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val dbRepository: TodoDBRepository,
    private val notifRepo: NotificationDBRepository
) : ViewModel() {

    val todosLiveData: LiveData<List<Todo>?> = dbRepository.todosLiveDate

    private val _filterLiveData: MutableLiveData<Filter?> = MutableLiveData()
    val filterLiveData: LiveData<Filter?> = _filterLiveData

    private val _goToTopLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val goToTopLiveData: LiveData<Boolean> = _goToTopLiveData

    val currentFilter: Filter?
        get() = filterLiveData.value

    val todosCount: Long
        get() = try {
            dbRepository.todosCount()
        } catch (e: InterruptedException) {
            0
        }

    val doneTodosCount: Long
        get() = try {
            dbRepository.doneTodosCount()
        } catch (e: InterruptedException) {
            0
        }

    val filterIndicatorVisibility: Int
        get() {
            return if (currentFilter != null && currentFilter?.filterIsEmpty() == false)
                View.VISIBLE
            else
                View.GONE
        }

    init {
        deleteShownNotification()
    }

    fun applyFilter(filter: Filter?) {
        _filterLiveData.value = filter
    }

    fun goToTop() {
        _goToTopLiveData.value = true
    }

    private fun deleteShownNotification() {
        //in startup get all is shown and delete it
        try {
            notifRepo.deleteShownNotifications()

            //for in notifs if arrive date < current time millis delete this
            val allNotifications: List<Notification>? = notifRepo.getAllNotifications()
            if (allNotifications != null) {
                for (notif: Notification in allNotifications) {
                    if (notif.arriveDate < System.currentTimeMillis()) //arrive date is passed
                        notifRepo.deleteNotification(notif)
                }
            }

        } catch (ignored: InterruptedException) {
        }
    }

    @JvmOverloads
    fun fetch(filter: Filter? = currentFilter) {
        if (filter == null) {
            dbRepository.fetchAll()
        } else {
            try {
                dbRepository.fetchWithFilter(filter)
            } catch (ignored: InterruptedException) {
            }
        }
    }

    fun addTodo(todo: Todo?): Long {
        var insertedRow: Long = 0

        try {
            insertedRow = dbRepository.addTodo(todo)
        } catch (ignored: InterruptedException) {
        }

        fetch()

        return insertedRow
    }

    fun editTodo(todo: Todo?) {
        try {
            dbRepository.editTodo(todo)
        } catch (ignored: InterruptedException) {
        }
        fetch()
    }

    fun deleteTodo(todo: Todo?) {
        try {
            dbRepository.deleteTodo(todo)
        } catch (ignored: InterruptedException) {
        }
        fetch()
    }

    fun deleteAllTodos() {
        try {
            dbRepository.deleteAllTodos()
        } catch (ignored: InterruptedException) {
        }
        applyFilter(null)
        fetch()
    }

    fun deleteAllDoneTodos() {
        try {
            dbRepository.deleteAllDoneTodos()
        } catch (ignored: InterruptedException) {
        }
        applyFilter(null)
        fetch()
    }

    fun todosIsEmpty(): Boolean {
        return todosCount == 0L
    }

    fun todosDoneIsEmpty(): Boolean {
        return doneTodosCount == 0L
    }

    fun setDoneTodo(todoID: Long) {
        try {
            dbRepository.setDoneTodo(todoID)
            notifRepo.setDoneTodo(todoID) //set done todo for notifications
        } catch (ignored: InterruptedException) {
        }
        fetch()
    }

    private fun pureValidateTodo(todo: Todo): Boolean {
        val todoTitle = todo.title
        return todoTitle != null && todoTitle.trim().isNotEmpty() && todoTitle.trim().isNotEmpty()
    }

    fun validateTodo(todo: Todo): String? {
        val isValidTodo = pureValidateTodo(todo)
        return if (!isValidTodo) "عنوان کار نمی تواند خالی باشد!" else null
    }

}