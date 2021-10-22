package viewmodel;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ir.hamsaa.persiandatepicker.date.PersianDateImpl;
import model.DateTime;
import model.Notification;
import repo.dbRepoController.NotificationDBRepository;
import repo.dbRepoController.TodoDBRepository;
import utils.Constants;
import utils.DateHelper;

public class ShowNotificationViewModel extends ViewModel {

    private final MutableLiveData<Boolean> closeLive;

    private Notification notification;

    public ShowNotificationViewModel() {
        closeLive = new MutableLiveData<>();
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
        return notification.getArriveDate() != 0;
    }

    public boolean hasCategory() {
        return notification.getCategoryId() != 0 && notification.getCategory() != null;
    }

    public String getDateReminder() {
        return getDate(notification.getArriveDate()).getPersianDate();
    }

    public String getClockReminder() {
        return getDate(notification.getArriveDate()).getClock();
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

        NotificationDBRepository dbRepository = new NotificationDBRepository();
        Notification notification = null;
        try {
            notification = dbRepository.getNotification(notifId);
        } catch (InterruptedException ignored) {
        }

        if (notification == null) {
            mustClose();
            return;
        }

        this.notification = notification;
    }

    public void deleteNotification() {
        if (notification == null)
            return;

        NotificationDBRepository notifRepo = new NotificationDBRepository();
        try {
            notifRepo.deleteNotification(notification);
        } catch (InterruptedException ignored) {
        }
    }

    public void done() {
        if (notification == null)
            return;

        TodoDBRepository todoRepo = new TodoDBRepository();
        try {
            todoRepo.setTodoIsDone(notification.getId());
        } catch (InterruptedException ignored) {
        }
    }


    public Notification getNotification() {
        return notification;
    }

    public LiveData<Boolean> getCloseLive() {
        return closeLive;
    }

    private void mustClose() {
        closeLive.setValue(true);
    }
}
