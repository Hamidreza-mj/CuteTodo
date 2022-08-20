package viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    val todosFlow: Flow<List<Todo>?> = dbRepository.getAllTodos()

    private val _filterStateFlow: MutableStateFlow<Filter?> = MutableStateFlow(null)
    val filterStateFlow: StateFlow<Filter?> = _filterStateFlow.asStateFlow()

    //--------------channels----------------
    private val _goToTopChannel: Channel<Boolean> = Channel()
    val goToTopFlow: Flow<Boolean> = _goToTopChannel.receiveAsFlow()

    val currentFilter: Filter?
        get() = _filterStateFlow.value

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

    suspend fun getTodosCount(): Long {
        var count = 0L

        viewModelScope.launch(Dispatchers.IO) {
            count = dbRepository.todosCount()
        }.join()

        return count
    }

    suspend fun todosCountIsEmpty(): Boolean {
        var isEmpty = false
        var todosCount = 0L

        viewModelScope.launch(Dispatchers.IO) {
            todosCount = getTodosCount()
            isEmpty = todosCount == 0L
        }.join()

        return isEmpty
    }

    suspend fun getDoneTodosCount(): Long {
        var count = 0L

        viewModelScope.launch(Dispatchers.IO) {
            count = dbRepository.doneTodosCount()
        }.join()

        return count
    }

    fun applyFilter(filter: Filter?) {
        _filterStateFlow.value = filter
    }

    fun goToTop() {
        viewModelScope.launch {
            _goToTopChannel.send(true)
        }
    }

    private fun deleteShownNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            //in startup get all is shown and delete it

            launch {
                notifRepo.deleteShownNotifications()

                //for in notifs if arrive date < current time millis delete this
                val allNotifications: List<Notification>? = notifRepo.getAllNotifications()

                if (allNotifications != null) {
                    for (notif: Notification in allNotifications!!) {
                        if (notif.arriveDate < System.currentTimeMillis()) //arrive date is passed
                            notifRepo.deleteNotification(notif)
                    }
                }

            }.join()
        }
    }

    @JvmOverloads
    fun fetch(filter: Filter? = currentFilter) {
        if (filter == null) {
            dbRepository.getAllTodos()
        } else {
            try {
                dbRepository.fetchWithFilter(filter)
            } catch (ignored: InterruptedException) {
            }
        }
    }

    suspend fun addTodo(todo: Todo?): Long {
        var insertedRow = 0L

        viewModelScope.launch(Dispatchers.IO) {
            insertedRow = dbRepository.addTodo(todo)
        }.join()

        return insertedRow
    }

    fun editTodo(todo: Todo?) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.editTodo(todo)
        }
    }

    fun deleteTodo(todo: Todo?) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.deleteTodo(todo)
        }
    }

    fun deleteAllTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.deleteAllTodos()
            applyFilter(null)
        }
    }

    fun deleteAllDoneTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.deleteAllDoneTodos()
            applyFilter(null)
        }
    }

    fun setDoneTodo(todoID: Long) = viewModelScope.launch(Dispatchers.IO) {
        launch {
            dbRepository.setDoneTodo(todoID)
            notifRepo.setDoneTodo(todoID) //set done todo for notifications}
        }.join()

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