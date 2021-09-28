package repo.dbRepoController;


import androidx.lifecycle.MutableLiveData;

import java.util.List;

import hlv.cute.todo.App;
import model.Filter;
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

    public void fetchAll() {
        //it must call be in another thread
        //use .postValue() instead of .setValue()
        // because the .postValue() run in the background thread (non-ui thread)
        new Thread(() -> todos.postValue(dao.getAllTodos())).start();
    }

    public void fetchWithFilter(Filter filter) throws InterruptedException {
        List<Todo.Priority> priorities = filter.getPriorities();
        if (priorities.isEmpty()/* && todosCount() != 0*/)
            priorities = filter.addAllPriorities();

        List<Todo.Priority> finalPriorities = priorities;

        if (filter.isDone() && filter.isUndone() || !filter.isDone() && !filter.isUndone()) {//not need check isDone
            new Thread(() ->
                    todos.postValue(dao.filterByAllTodos(finalPriorities))).start();

        } else if (filter.isDone()) {
            new Thread(() ->
                    todos.postValue(dao.filterByDoneTodos(true, finalPriorities))).start();

        } else if (filter.isUndone()) {
            new Thread(() ->
                    todos.postValue(dao.filterByDoneTodos(false, finalPriorities))).start();
        }
    }

    public void addTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> dao.create(todo));
        thread.start();
        thread.join();
    }

    public void editTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> dao.update(todo));
        thread.start();
        thread.join();
    }

    public void deleteTodo(Todo todo) throws InterruptedException {
        Thread thread = new Thread(() -> dao.delete(todo));
        thread.start();
        thread.join();
    }

    public void deleteAllTodos() throws InterruptedException {
        Thread thread = new Thread(dao::deleteAllTodos);
        thread.start();
        thread.join();
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
    }

    public MutableLiveData<List<Todo>> getTodosLiveDate() {
        return todos;
    }
}