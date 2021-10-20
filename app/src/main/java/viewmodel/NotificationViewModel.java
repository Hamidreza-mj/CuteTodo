package viewmodel;

import androidx.lifecycle.ViewModel;

import model.Notification;
import repo.dbRepoController.NotificationDBRepository;

public class NotificationViewModel extends ViewModel {

    private final NotificationDBRepository dbRepository;

    public NotificationViewModel() {
        dbRepository = new NotificationDBRepository();
    }

    public void addNotification(Notification notification) {
        try {
            dbRepository.addNotification(notification);
        } catch (InterruptedException ignored) {
        }
    }

    public void editNotification(Notification notification) {
        try {
            dbRepository.editNotification(notification);
        } catch (InterruptedException ignored) {
        }
    }

    public void deleteNotification(Notification notification) {
        try {
            dbRepository.deleteNotification(notification);
        } catch (InterruptedException ignored) {
        }
    }

    public void deleteAllNotifications() {
        try {
            dbRepository.deleteAllNotifications();
        } catch (InterruptedException ignored) {
        }
    }

    public long getNotificationsCount() {
        try {
            return dbRepository.getNotificationsCount();
        } catch (InterruptedException e) {
            return 0;
        }
    }


}
