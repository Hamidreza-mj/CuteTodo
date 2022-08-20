package repo.dbRepoController

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.*
import model.Filter
import model.Priority
import model.Todo
import repo.dao.TodoDao
import utils.DateHelper
import javax.inject.Inject

@ViewModelScoped
class TodoDBRepository @Inject constructor(
    private val todoDao: TodoDao
) {

    fun getAllTodos(): Flow<List<Todo>?> =
        todoDao.getAllTodos().distinctUntilChanged()

    private val _todosStateFlow: MutableStateFlow<List<Todo>?> = MutableStateFlow(null)
    val todosStateFlow: StateFlow<List<Todo>?> = _todosStateFlow.asStateFlow()

    fun fetchWithFilter(filter: Filter) {
        var priorities: List<Priority?> = filter.priorities

        if (priorities.isEmpty())
            priorities = filter.addAllPriorities()

        var filteredTodos = mutableListOf<Todo>()

        if (filter.isDone && filter.isUndone || !filter.isDone && !filter.isUndone) { //not need check isDone
            Thread {
                filteredTodos = todoDao.filterByAllTodos(priorities) as MutableList<Todo>
            }.apply {
                start()
                join()
            }

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
            Thread {
                filteredTodos = todoDao.filterByDoneTodos(true, priorities) as MutableList<Todo>
            }.apply {
                start()
                join()
            }

            if (filter.isScheduled)
                filterByScheduled(filteredTodos)

            if (filter.isToday)
                filterByToday(filteredTodos)

            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos, filter.categoryIds)


        } else if (filter.isUndone) {
            Thread {
                filteredTodos = todoDao.filterByDoneTodos(false, priorities) as MutableList<Todo>
            }.apply {
                start()
                join()
            }

            if (filter.isScheduled)
                filterByScheduled(filteredTodos)

            if (filter.isToday)
                filterByToday(filteredTodos)

            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos, filter.categoryIds)
        }

        _todosStateFlow.value = filteredTodos
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

    suspend fun todosCount(): Long =
        todoDao.getTodosCount()

    suspend fun doneTodosCount(): Long {
        return todoDao.getDoneTodosCount()
    }

    suspend fun getTodo(todoID: Long): Todo? {
        return todoDao.getTodo(todoID)
    }

    suspend fun addTodo(todo: Todo?): Long {
        return todoDao.create(todo)
    }

    suspend fun editTodo(todo: Todo?) {
        todoDao.update(todo)
    }

    suspend fun deleteTodo(todo: Todo?) {
        todoDao.delete(todo)
    }

    suspend fun deleteAllTodos() {
        todoDao.deleteAllTodos()
    }

    suspend fun deleteAllDoneTodos() {
        todoDao.deleteAllDoneTodo()
    }

    suspend fun setDoneTodo(todoID: Long) {
        todoDao.setDoneTodo(todoID)
    }

    suspend fun setTodoIsDone(todoID: Long) {
        todoDao.setTodoIsDone(todoID)
    }
}