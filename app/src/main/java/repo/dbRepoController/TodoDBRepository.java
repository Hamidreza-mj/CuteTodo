package repo.dbRepoController;


import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import hlv.cute.todo.App;
import model.Filter;
import model.Priority;
import model.Todo;
import repo.dao.TodoDao;
import utils.DateHelper;

public class TodoDBRepository {

    private final TodoDao dao;
    private final MutableLiveData<List<Todo>> todos;

    private Todo todo;

    private long count = 0;
    private long doneCount = 0;

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
        List<Priority> priorities = filter.getPriorities();
        if (priorities.isEmpty()/* && todosCount() != 0*/)
            priorities = filter.addAllPriorities();

        List<Priority> finalPriorities = priorities;

        AtomicReference<List<Todo>> filteredTodos = new AtomicReference<>();

        if (filter.isDone() && filter.isUndone() || !filter.isDone() && !filter.isUndone()) {//not need check isDone
            Thread thread = new Thread(() ->
                    filteredTodos.set(dao.filterByAllTodos(finalPriorities))
            );
            thread.start();
            thread.join();


            //need to reverse for or iterator (because the main list index has changed)
            //need to starts from end or use the way that independent on index
                /*for (int i = filteredTodos.get().size() - 1; i >= 0; i--)
                    Todo currentTodo = filteredTodos.get().get(i);
                    if (currentTodo.getArriveDate() == 0)
                        filteredTodos.get().remove(currentTodo);*/

            if (filter.isScheduled())
                filterByScheduled(filteredTodos.get());


            if (filter.isToday())
                filterByToday(filteredTodos.get());


            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos.get(), filter.getCategoryIds());

        } else if (filter.isDone()) {
            Thread thread = new Thread(() ->
                    filteredTodos.set(dao.filterByDoneTodos(true, finalPriorities))
            );
            thread.start();
            thread.join();

            if (filter.isScheduled())
                filterByScheduled(filteredTodos.get());

            if (filter.isToday())
                filterByToday(filteredTodos.get());

            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos.get(), filter.getCategoryIds());

        } else if (filter.isUndone()) {
            Thread thread = new Thread(() ->
                    filteredTodos.set(dao.filterByDoneTodos(false, finalPriorities))
            );
            thread.start();
            thread.join();

            if (filter.isScheduled())
                filterByScheduled(filteredTodos.get());

            if (filter.isToday())
                filterByToday(filteredTodos.get());

            if (filter.needToFilterByCategory())
                filterByCategory(filteredTodos.get(), filter.getCategoryIds());
        }

        todos.postValue(filteredTodos.get());
    }

    private void filterByCategory(List<Todo> filteredTodos, List<Integer> categoryIds) {
        if (filteredTodos == null)
            return;

        List<Todo> newTodos = new ArrayList<>();

        for (Todo todo : filteredTodos) {
            if (categoryIds.contains(todo.getCategoryId()))
                newTodos.add(todo);
        }

        filteredTodos.clear();
        filteredTodos.addAll(newTodos);
    }

    private void filterByScheduled(List<Todo> filteredTodos) {
        if (filteredTodos == null)
            return;

        List<Todo> newTodos = new ArrayList<>();

        for (Todo todo : filteredTodos) {
            if (todo.getArriveDate() != 0)
                newTodos.add(todo);
        }

        filteredTodos.clear();
        filteredTodos.addAll(newTodos);
    }

    private void filterByToday(List<Todo> filteredTodos) {
        if (filteredTodos == null)
            return;

        List<Todo> newTodos = new ArrayList<>();

        //filter by scheduled todos
        filterByScheduled(filteredTodos);

        for (Todo todo : filteredTodos) {
            DateHelper todoDate = new DateHelper(todo.getArriveDate());
            DateHelper nowDate = new DateHelper(System.currentTimeMillis());

            int todoYear = todoDate.getYear();
            int todoMonth = todoDate.getMonth();
            int todoDay = todoDate.getDay();

            int nowYear = nowDate.getYear();
            int nowMonth = nowDate.getMonth();
            int nowDay = nowDate.getDay();


            if (todoYear == nowYear && todoMonth == nowMonth && todoDay == nowDay)
                newTodos.add(todo);
        }

        filteredTodos.clear();
        filteredTodos.addAll(newTodos);
    }

    public long addTodo(Todo todo) throws InterruptedException {
        final long[] insertedRow = {0};
        Thread thread = new Thread(() -> insertedRow[0] = dao.create(todo));
        thread.start();
        thread.join();
        return insertedRow[0];
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

    public void deleteAllDoneTodos() throws InterruptedException {
        Thread thread = new Thread(dao::deleteAllDoneTodo);
        thread.start();
        thread.join();
    }

    public long todosCount() throws InterruptedException {
        Thread thread = new Thread(() -> count = dao.getTodosCount());
        thread.start();
        thread.join();
        return count;
    }

    public long doneTodosCount() throws InterruptedException {
        Thread thread = new Thread(() -> doneCount = dao.getDoneTodosCount());
        thread.start();
        thread.join();
        return doneCount;
    }

    public void setDoneTodo(long todoID) throws InterruptedException {
        Thread thread = new Thread(() -> dao.setDoneTodo(todoID));
        thread.start();
        thread.join();
    }

    public Todo getTodo(long todoID) throws InterruptedException {
        Thread thread = new Thread(() -> todo = dao.getTodo(todoID));
        thread.start();
        thread.join();
        return todo;
    }


    public void setTodoIsDone(long todoID) throws InterruptedException {
        Thread thread = new Thread(() -> dao.setTodoIsDone(todoID));
        thread.start();
        thread.join();
    }

    public MutableLiveData<List<Todo>> getTodosLiveDate() {
        return todos;
    }
}