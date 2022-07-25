package repo.dbRepoController

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hlv.cute.todo.App
import model.Filter
import model.Priority
import model.Todo
import repo.dao.TodoDao
import utils.DateHelper

class TodoDBRepository {

    private val dao: TodoDao = App.get()!!.todoDatabase()!!.todoDao!!

    private val _todosLiveDate: MutableLiveData<List<Todo>?> = MutableLiveData()
    val todosLiveDate: LiveData<List<Todo>?> = _todosLiveDate

    fun fetchAll() {
        //it must call be in another thread
        //use .postValue() instead of .setValue()
        // because the .postValue() run in the background thread (non-ui thread)
        Thread {
            _todosLiveDate.postValue(dao.getAllTodos())
        }.start()
    }

    @Throws(InterruptedException::class)
    fun fetchWithFilter(filter: Filter) {
        var priorities: List<Priority?> = filter.priorities

        if (priorities.isEmpty())
            priorities = filter.addAllPriorities()

        var filteredTodos = mutableListOf<Todo>()

        if (filter.isDone && filter.isUndone || !filter.isDone && !filter.isUndone) { //not need check isDone
            Thread {
                filteredTodos = dao.filterByAllTodos(priorities) as MutableList<Todo>
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
                filteredTodos = dao.filterByDoneTodos(true, priorities) as MutableList<Todo>
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
                filteredTodos = dao.filterByDoneTodos(false, priorities) as MutableList<Todo>
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

        _todosLiveDate.postValue(filteredTodos)
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

    @Throws(InterruptedException::class)
    fun addTodo(todo: Todo?): Long {
        var insertedRow: Long = 0

        Thread {
            insertedRow = dao.create(todo)
        }.apply {
            start()
            join()
        }

        return insertedRow
    }

    @Throws(InterruptedException::class)
    fun editTodo(todo: Todo?) {
        Thread {
            dao.update(todo)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun deleteTodo(todo: Todo?) {
        Thread {
            dao.delete(todo)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun deleteAllTodos() {
        Thread {
            dao.deleteAllTodos()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun deleteAllDoneTodos() {
        Thread {
            dao.deleteAllDoneTodo()
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun todosCount(): Long {
        var count: Long = 0

        Thread {
            count = dao.getTodosCount()
        }.apply {
            start()
            join()
        }

        return count
    }

    @Throws(InterruptedException::class)
    fun doneTodosCount(): Long {
        var doneCount: Long = 0
        Thread {
            doneCount = dao.getDoneTodosCount()
        }.apply {
            start()
            join()
        }

        return doneCount
    }

    @Throws(InterruptedException::class)
    fun setDoneTodo(todoID: Long) {
        Thread {
            dao.setDoneTodo(todoID)
        }.apply {
            start()
            join()
        }
    }

    @Throws(InterruptedException::class)
    fun getTodo(todoID: Long): Todo? {
        var todo: Todo? = null

        Thread {
            todo = dao.getTodo(todoID)
        }.apply {
            start()
            join()
        }

        return todo
    }

    @Throws(InterruptedException::class)
    fun setTodoIsDone(todoID: Long) {
        Thread {
            dao.setTodoIsDone(todoID)
        }.apply {
            start()
            join()
        }
    }
}