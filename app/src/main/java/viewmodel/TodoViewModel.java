package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Todo;
import repo.dbRepoController.TodoDBRepository;

public class TodoViewModel extends ViewModel {

    private final TodoDBRepository dbRepository;
    private final MutableLiveData<List<Todo>> todosLiveDate;

    private final MutableLiveData<Boolean> goToTopLiveData;

    public TodoViewModel() {
        dbRepository = new TodoDBRepository();
        todosLiveDate = dbRepository.getTodosLiveDate();
        goToTopLiveData = new MutableLiveData<>();
    }

    public void fetch() {
        dbRepository.fetchAllTodos();
    }

    public void addTodo(Todo todo) {
        try {
            dbRepository.addTodo(todo);
        } catch (InterruptedException ignored) {
        }
    }

    public void editTodo(Todo todo) {
        try {
            dbRepository.editTodo(todo);
        } catch (InterruptedException ignored) {
        }
    }

    public void deleteTodo(Todo todo) {
        try {
            dbRepository.deleteTodo(todo);
        } catch (InterruptedException ignored) {
        }
    }

    public void deleteAllTodos() {
        try {
            dbRepository.deleteAllTodos();
        } catch (InterruptedException ignored) {
        }
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

    public LiveData<List<Todo>> getTodosLiveDate() {
        //values will not be emitted unless they have changed.
        return Transformations.distinctUntilChanged(todosLiveDate);
    }

    public void goToTop() {
        if (goToTopLiveData != null)
            goToTopLiveData.setValue(true);
    }

    public LiveData<Boolean> getGoToTopLiveData() {
        return goToTopLiveData;
    }
}
