package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Todo;
import repo.TodoDBRepository;

public class TodoViewModel extends ViewModel {

    private final TodoDBRepository dbRepository;
    private final MutableLiveData<List<Todo>> todosLiveDate;

    public TodoViewModel() {
        dbRepository = new TodoDBRepository();
        todosLiveDate = dbRepository.getTodosLiveDate();
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
            dbRepository.deleteAllTodo();
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

    public boolean pureValidateTodo(Todo todo) {
        String todoTitle = todo.getTitle();
        return todoTitle != null && !todoTitle.trim().isEmpty() && todoTitle.trim().length() != 0;
    }

    public String validateTodo(Todo todo) {
        boolean isValidTodo = pureValidateTodo(todo);

        if (!isValidTodo)
            return "عنوان کار نامعتبر است!";

        return null;
    }

    public LiveData<List<Todo>> getTodosLiveDate() {
        return todosLiveDate;
    }
}
