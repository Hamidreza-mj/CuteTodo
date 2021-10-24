package viewmodel;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ir.hamsaa.persiandatepicker.date.PersianDateImpl;
import model.DateTime;
import model.Notification;
import model.Todo;
import repo.dbRepoController.NotificationDBRepository;
import repo.dbRepoController.TodoDBRepository;
import utils.Constants;
import utils.DateHelper;

public class ShowNotificationViewModel extends ViewModel {

    private final MutableLiveData<Notification> notificationLiveData;
    private final MutableLiveData<Boolean> closeLive;
    private final MutableLiveData<Boolean> runMainLive;
    private final NotificationDBRepository dbRepository;
    private final TodoDBRepository todoRepository;

    public ShowNotificationViewModel() {
        notificationLiveData = new MutableLiveData<>();
        closeLive = new MutableLiveData<>();
        runMainLive = new MutableLiveData<>();
        dbRepository = new NotificationDBRepository();
        todoRepository = new TodoDBRepository();
    }

    public void setIntent(Intent intent) {
        handleExtras(intent);
    }

    public int getLytDateVisibility() {
        return hasArriveDate() ? View.VISIBLE : View.GONE;
    }

    public int getLytCategoryVisibility() {
        return hasCategory() ? View.VISIBLE : View.GONE;
    }

    public boolean hasArriveDate() {
        if (getNotification() == null)
            return false;

        return getNotification().getArriveDate() != 0;
    }

    public boolean hasCategory() {
        if (getNotification() == null)
            return false;

        return getNotification().getCategoryId() != 0 && getNotification().getCategory() != null;
    }

    public String getDateReminder() {
        if (getNotification() == null)
            return "";

        return getDate(getNotification().getArriveDate()).getPersianDate();
    }

    public String getClockReminder() {
        if (getNotification() == null)
            return "";

        return getDate(getNotification().getArriveDate()).getClock();
    }

    private DateTime getDate(long timeMillis) {
        DateTime dateTime = new DateTime();

        DateHelper dateHelper = new DateHelper(timeMillis);
        dateTime.setHour(dateHelper.getHour());
        dateTime.setMinute(dateHelper.getMinute());

        PersianDateImpl persianDate = new PersianDateImpl();
        persianDate.setDate(timeMillis);

        dateTime.setDate(persianDate);

        return dateTime;
    }

    private void handleExtras(Intent intent) {
        if (!intent.hasExtra(Constants.Keys.NOTIF_ID_DETAIL)) {
            mustClose();
            return;
        }

        int notifId = intent.getIntExtra(Constants.Keys.NOTIF_ID_DETAIL, 0);
        if (notifId == 0) {
            mustClose();
            return;
        }


        Notification notification = null;
        try {
            notification = dbRepository.getNotification(notifId);
        } catch (InterruptedException ignored) {
        }

        if (notification == null) {
            try {
                Todo todo = todoRepository.getTodo(notifId);
                if (todo != null) {
                    Notification newNotification = new Notification();
                    newNotification.initWith(todo);
                    notificationLiveData.setValue(newNotification);
                    return;
                }
            } catch (InterruptedException ignored) {
            }

            runMainLive.setValue(true); //run main if notification or todo == null
            return;
        }

        notificationLiveData.setValue(notification);
    }

    public void deleteNotification() {
        if (getNotification() == null)
            return;

        try {
            dbRepository.deleteNotification(getNotification());
        } catch (InterruptedException ignored) {
        }
    }

    public void done() {
        if (getNotification() == null)
            return;

        try {
            todoRepository.setTodoIsDone(getNotification().getId());
        } catch (InterruptedException ignored) {
        }
    }

    public void setShown() {
        if (getNotification() == null)
            return;

        try {
            dbRepository.setShownTodo(getNotification().getId());
        } catch (InterruptedException ignored) {
        }
    }

    public Notification getNotification() {
        return notificationLiveData.getValue();
    }

    public LiveData<Notification> getNotificationLive() {
        return notificationLiveData;
    }

    public LiveData<Boolean> getCloseLive() {
        return closeLive;
    }

    public LiveData<Boolean> getRunMainLive() {
        return runMainLive;
    }

    private void mustClose() {
        closeLive.setValue(true);
    }
}
