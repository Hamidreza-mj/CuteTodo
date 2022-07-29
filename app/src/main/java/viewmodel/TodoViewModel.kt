package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Filter;
import model.Notification;
import model.Todo;
import repo.dbRepoController.NotificationDBRepository;
import repo.dbRepoController.TodoDBRepository;

public class TodoViewModel extends ViewModel {

    private final TodoDBRepository dbRepository;
    private final NotificationDBRepository notifRepo;

    private final LiveData<List<Todo>> todosLiveDate;
    private final MutableLiveData<Filter> filterLiveData;
    private final MutableLiveData<Boolean> goToTopLiveData;

    public TodoViewModel() {
        dbRepository = new TodoDBRepository();
        notifRepo = new NotificationDBRepository();
        todosLiveDate = dbRepository.getTodosLiveDate();
        filterLiveData = new MutableLiveData<>();
        goToTopLiveData = new MutableLiveData<>();
        deleteShownNotification();
    }

    private void deleteShownNotification() {
        //in startup get all is shown and delete it
        NotificationDBRepository repository = new NotificationDBRepository();
        try {
            repository.deleteShownNotifications();

            //for in notifs if arrive date < current time millis delete this
            List<Notification> allNotifications = repository.getAllNotifications();
            for (Notification notification : allNotifications) {
                if (notification.getArriveDate() < System.currentTimeMillis()) //arrive date is passed
                    repository.deleteNotification(notification);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void fetch(Filter filter) {
        if (filter == null)
            dbRepository.fetchAll();
        else {
            try {
                dbRepository.fetchWithFilter(filter);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void fetch() {
        fetch(getCurrentFilter());
    }

    public long addTodo(Todo todo) {
        long insertedRow = 0;
        try {
            insertedRow = dbRepository.addTodo(todo);
        } catch (InterruptedException ignored) {
        }

        fetch();
        return insertedRow;
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

    public void deleteAllDoneTodos() {
        try {
            dbRepository.deleteAllDoneTodos();
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

    public long getDoneTodosCount() {
        try {
            return dbRepository.doneTodosCount();
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public boolean todosIsEmpty() {
        return getTodosCount() == 0;
    }

    public boolean todosDoneIsEmpty() {
        return getDoneTodosCount() == 0;
    }

    public void setDoneTodo(long todoID) {
        try {
            dbRepository.setDoneTodo(todoID);
            notifRepo.setDoneTodo(todoID); //set done todo for notifications
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

    public LiveData<List<Todo>> getTodosLiveData() {
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
