package repo.dbRepoController

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import model.Priority
import model.Todo
import repo.dao.TodoDao
import javax.inject.Inject

@ViewModelScoped
class TodoDBRepository @Inject constructor(
    private val todoDao: TodoDao
) {

    fun getAllTodos(): Flow<List<Todo>?> =
        todoDao.getAllTodos().distinctUntilChanged()

    suspend fun todosCount(): Long {
        return todoDao.getTodosCount()
    }

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

    suspend fun filterByDoneTodos(isDone: Boolean, priorities: List<Priority?>?): List<Todo>? {
        return todoDao.filterByDoneTodos(isDone, priorities)
    }

    suspend fun filterByAllTodos(priorities: List<Priority?>?): List<Todo>? {
        return todoDao.filterByAllTodos(priorities)
    }

}