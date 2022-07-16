package repo.dbRepoController

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import hlv.cute.todo.App
import model.Todo
import repo.dao.TodoDao

class SearchDBRepository {

    private val dao: TodoDao = App.get()!!.todoDatabase()!!.todoDao!!
    private val _todosLive: MutableLiveData<List<Todo>?> = MutableLiveData()
    val todosLive: LiveData<List<Todo>?> = _todosLive

    fun initFetch() {
        Thread {
            _todosLive.postValue(dao.getAllTodos())
        }.start()
    }

    fun initFetch(categoryId: Int) {
        Thread {
            _todosLive.postValue(dao.getTodosWithCategory(categoryId))
        }.start()
    }

    fun searchTodo(term: String?) {
        Thread {
            _todosLive.postValue(dao.searchTodo(term))
        }.start()
    }

    fun searchTodo(term: String?, categoryId: Int) {
        Thread {
            _todosLive.postValue(dao.searchTodoWithCategoryId(term, categoryId))
        }.start()
    }

    fun searchCategory(term: String?) {
        Thread {
            _todosLive.postValue(dao.searchCategory(term))
        }.start()
    }

    fun searchTodoWithCategory(term: String?) {
        Thread {
            _todosLive.postValue(dao.searchTodoWithCategory(term))
        }.start()
    }

}