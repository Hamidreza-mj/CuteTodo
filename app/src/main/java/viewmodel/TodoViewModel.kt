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
import model.Priority
import model.Todo
import repo.dbRepoController.NotificationDBRepository
import repo.dbRepoController.TodoDBRepository
import utils.DateHelper
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val dbRepository: TodoDBRepository,
    private val notifRepo: NotificationDBRepository
) : ViewModel() {

    private val _todosSharedFlow = MutableSharedFlow<List<Todo>?>()
    val todosFlow: Flow<List<Todo>?> = _todosSharedFlow.asSharedFlow()

    private val _filterStateFlow: MutableStateFlow<Filter?> = MutableStateFlow(null)
    val filterStateFlow: Flow<Filter?> = _filterStateFlow.asStateFlow()

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
        var todosCount: Long

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
                    for (notif: Notification in allNotifications) {
                        if (notif.arriveDate < System.currentTimeMillis()) //arrive date is passed
                            notifRepo.deleteNotification(notif)
                    }
                }

            }.join()
        }
    }

    fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            _todosSharedFlow.emitAll(dbRepository.getAllTodos())
        }
    }

    suspend fun fetchWithFilter(filter: Filter): List<Todo>? {
        var priorities: List<Priority?> = filter.priorities

        if (priorities.isEmpty())
            priorities = filter.addAllPriorities()

        var filteredTodos: ArrayList<Todo>? = ArrayList()

        if (filter.isDone && filter.isUndone || !filter.isDone && !filter.isUndone) { //not need check isDone
            viewModelScope.launch(Dispatchers.IO) {
                filteredTodos = dbRepository.filterByAllTodos(priorities) as ArrayList<Todo>?
            }.join()

            //need to reverse for or iterator (because the main list index has changed)
            //need to starts from end or use the way that independent on index
            /*for (int i = filteredTodos.get().size() - 1; i >= 0; i--)
                    Todo currentTodo = filteredTodos.get().get(i);
                    if (currentTodo.getArriveDate() == 0)
                        filteredTodos.get().remove(currentTodo);*/

            if (filter.isScheduled)
                filterByScheduled(filteredTodos)

            if (filter.isToday)
                filterByToday(filteredTodos)

            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos, filter.categoryIds)

        } else if (filter.isDone) {
            viewModelScope.launch(Dispatchers.IO) {
                filteredTodos =
                    dbRepository.filterByDoneTodos(true, priorities) as ArrayList<Todo>?
            }.join()

            if (filter.isScheduled)
                filterByScheduled(filteredTodos)

            if (filter.isToday)
                filterByToday(filteredTodos)

            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos, filter.categoryIds)


        } else if (filter.isUndone) {
            viewModelScope.launch(Dispatchers.IO) {
                filteredTodos =
                    dbRepository.filterByDoneTodos(false, priorities) as ArrayList<Todo>?
            }.join()

            if (filter.isScheduled)
                filterByScheduled(filteredTodos)

            if (filter.isToday)
                filterByToday(filteredTodos)

            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos, filter.categoryIds)
        }

        return filteredTodos
    }

    private fun filterByCategory(filteredTodos: MutableList<Todo>?, categoryIds: List<Int>?) {
        if (filteredTodos == null)
            return

        val newTodos: MutableList<Todo> = mutableListOf()

        for (todo in filteredTodos) {
            if (categoryIds!!.contains(todo.categoryId))
                newTodos.add(todo)
        }

        filteredTodos.clear()
        filteredTodos.addAll(newTodos)
    }

    private fun filterByScheduled(filteredTodos: MutableList<Todo>?) {
        if (filteredTodos == null)
            return

        val newTodos: MutableList<Todo> = mutableListOf()

        for (todo in filteredTodos) {
            if (todo.arriveDate != 0L)
                newTodos.add(todo)
        }

        filteredTodos.clear()
        filteredTodos.addAll(newTodos)
    }

    private fun filterByToday(filteredTodos: MutableList<Todo>?) {
        if (filteredTodos == null)
            return

        val newTodos: MutableList<Todo> = mutableListOf()

        //filter by scheduled todos
        filterByScheduled(filteredTodos)

        for (todo in filteredTodos) {
            val todoDate = DateHelper(todo.arriveDate)
            val nowDate = DateHelper(System.currentTimeMillis())

            val todoYear = todoDate.getYear()
            val todoMonth = todoDate.getMonth()
            val todoDay = todoDate.getDay()
            val nowYear = nowDate.getYear()
            val nowMonth = nowDate.getMonth()
            val nowDay = nowDate.getDay()

            if (todoYear == nowYear && todoMonth == nowMonth && todoDay == nowDay)
                newTodos.add(todo)
        }

        filteredTodos.clear()
        filteredTodos.addAll(newTodos)
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