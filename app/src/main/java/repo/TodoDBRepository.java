package repo;


import androidx.lifecycle.MutableLiveData;

import java.util.List;

import hlv.cute.todo.App;
import model.Todo;
import repo.dao.TodoDao;

public class TodoDBRepository {

    private final TodoDao todoDao;
    private final MutableLiveData<List<Todo>> todos;

    private long count = 0;

    public TodoDBRepository() {
        todoDao = App.get().todoDatabase().getTodoDao();
        todos = new MutableLiveData<>();
    }

    public void fetchAllTodos() {
        //it must call be in another thread
        //use .postValue() instead of .setValue()
        // because the .postValue() run in the background thread (non-ui thread)
        new Thread(() -> todos.postValue(todoDao.getAllTodos())).start();
    }

    public void addTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> todoDao.create(todo));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public void editTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> todoDao.update(todo));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public void deleteTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> todoDao.delete(todo));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public void deleteAllTodo() throws InterruptedException {
        Thread thread = new Thread(todoDao::deleteAllTodos);
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public long todosCount() throws InterruptedException {
        Thread thread = new Thread(() -> count = todoDao.getTodosCount());
        thread.start();
        thread.join();
        return count;
    }

    public void setDoneTodo(long todoID) throws InterruptedException {
        Thread thread = new Thread(() -> todoDao.setDoneTodo(todoID));
        thread.start();
        thread.join();

        fetchAllTodos();
    }

    public MutableLiveData<List<Todo>> getTodosLiveDate() {
        return todos;
    }
}