package repo.dbRepoController

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.scopes.ViewModelScoped
import model.Todo
import repo.dao.TodoDao
import javax.inject.Inject

@ViewModelScoped
class SearchDBRepository @Inject constructor(
    private val todoDao: TodoDao
) {

    private val _todosLive: MutableLiveData<List<Todo>?> = MutableLiveData()
    val todosLive: LiveData<List<Todo>?> = _todosLive

    fun initFetch() {
        Thread {
            _todosLive.postValue(todoDao.getAllTodos())
        }.start()
    }

    fun initFetch(categoryId: Int) {
        Thread {
            _todosLive.postValue(todoDao.getTodosWithCategory(categoryId))
        }.start()
    }

    fun searchTodo(term: String?) {
        Thread {
            _todosLive.postValue(todoDao.searchTodo(term))
        }.start()
    }

    fun searchTodo(term: String?, categoryId: Int) {
        Thread {
            _todosLive.postValue(todoDao.searchTodoWithCategoryId(term, categoryId))
        }.start()
    }

    fun searchCategory(term: String?) {
        Thread {
            _todosLive.postValue(todoDao.searchCategory(term))
        }.start()
    }

    fun searchTodoWithCategory(term: String?) {
        Thread {
            _todosLive.postValue(todoDao.searchTodoWithCategory(term))
        }.start()
    }

}