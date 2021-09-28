package repo.dbRepoController;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import hlv.cute.todo.App;
import model.Todo;
import repo.dao.TodoDao;

public class SearchDBRepository {

    private final TodoDao dao;
    private final MutableLiveData<List<Todo>> todosLive;

    public SearchDBRepository() {
        dao = App.get().todoDatabase().getTodoDao();
        todosLive = new MutableLiveData<>();
    }

    public void initFetch() {
        new Thread(() -> todosLive.postValue(dao.getAllTodos())).start();
    }

    public void searchTodo(String term) {
        new Thread(() -> todosLive.postValue(dao.searchTodo(term))).start();

    }

    public void searchCategory(String term) {
        new Thread(() -> todosLive.postValue(dao.searchCategory(term))).start();
    }

    public void searchTodoWithCategory(String term) {
        new Thread(() -> todosLive.postValue(dao.searchTodoWithCategory(term))).start();
    }

    public LiveData<List<Todo>> getTodosLive() {
        return todosLive;
    }
}
