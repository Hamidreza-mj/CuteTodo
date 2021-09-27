package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Filter;
import model.Todo;
import repo.dbRepoController.TodoDBRepository;

public class TodoViewModel extends ViewModel {

    private final TodoDBRepository dbRepository;

    private final MutableLiveData<List<Todo>> todosLiveDate;
    private final MutableLiveData<Filter> filterLiveData;
    private final MutableLiveData<Boolean> goToTopLiveData;

    public TodoViewModel() {
        dbRepository = new TodoDBRepository();
        todosLiveDate = dbRepository.getTodosLiveDate();
        filterLiveData = new MutableLiveData<>();
        goToTopLiveData = new MutableLiveData<>();
    }

    public void fetch(Filter filter) {
        if (filter == null)
            dbRepository.fetchAll();
        else
            dbRepository.fetchWithFilter(filter);
    }

    public void fetch() {
        fetch(getCurrentFilter());
    }

    public void addTodo(Todo todo) {
        try {
            dbRepository.addTodo(todo);
        } catch (InterruptedException ignored) {
        }
        fetch();
    }

    public void editTodo(Todo todo) {
        try {
            dbRepository.editTodo(todo);
        } catch (InterruptedException ignored) {
        }
        fetch();
    }

    public void deleteTodo(Todo todo) {
        try {
            dbRepository.deleteTodo(todo);
        } catch (InterruptedException ignored) {
        }
        fetch();
    }

    public void deleteAllTodos() {
        try {
            dbRepository.deleteAllTodos();
        } catch (InterruptedException ignored) {
        }
        applyFilter(null);
        fetch();
    }

    public long getTodosCount() {
        try {
            return dbRepository.todosCount();
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public boolean todosIsEmpty() {
        return getTodosCount() == 0;
    }

    public void setDoneTodo(long todoID) {
        try {
            dbRepository.setDoneTodo(todoID);
        } catch (InterruptedException ignored) {
        }
        fetch();
    }

    public boolean pureValidateTodo(Todo todo) {
        String todoTitle = todo.getTitle();
        return todoTitle != null && !todoTitle.trim().isEmpty() && todoTitle.trim().length() != 0;
    }

    public String validateTodo(Todo todo) {
        boolean isValidTodo = pureValidateTodo(todo);

        if (!isValidTodo)
            return "عنوان کار نمی تواند خالی باشد!";

        return null;
    }

    public void applyFilter(Filter filter) {
        if (filterLiveData != null)
            filterLiveData.setValue(filter);
    }

    public void goToTop() {
        if (goToTopLiveData != null)
            goToTopLiveData.setValue(true);
    }

    public LiveData<List<Todo>> getTodosLiveDate() {
        return todosLiveDate;
    }

    public LiveData<Filter> getFilterLiveData() {
        return filterLiveData;
    }

    public Filter getCurrentFilter() {
        return getFilterLiveData().getValue();
    }

    public LiveData<Boolean> getGoToTopLiveData() {
        return goToTopLiveData;
    }
}
