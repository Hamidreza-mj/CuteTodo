package repo.dbRepoController;


import androidx.lifecycle.MutableLiveData;

import java.util.List;

import hlv.cute.todo.App;
import model.Todo;
import repo.dao.TodoDao;

public class TodoDBRepository {

    private final TodoDao dao;
    private final MutableLiveData<List<Todo>> todos;

    private long count = 0;

    public TodoDBRepository() {
        dao = App.get().todoDatabase().getTodoDao();
        todos = new MutableLiveData<>();
    }

    public void fetchAllTodos() {
        //it must call be in another thread
        //use .postValue() instead of .setValue()
        // because the .postValue() run in the background thread (non-ui thread)
        new Thread(() -> todos.postValue(dao.getAllTodos())).start();
    }

    public void addTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> dao.create(todo));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public void editTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> dao.update(todo));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public void deleteTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> dao.delete(todo));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public void deleteAllTodos() throws InterruptedException {
        Thread thread = new Thread(dao::deleteAllTodos);
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public long todosCount() throws InterruptedException {
        Thread thread = new Thread(() -> count = dao.getTodosCount());
        thread.start();
        thread.join();
        return count;
    }

    public void setDoneTodo(long todoID) throws InterruptedException {
        Thread thread = new Thread(() -> dao.setDoneTodo(todoID));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public MutableLiveData<List<Todo>> getTodosLiveDate() {
        return todos;
    }
}