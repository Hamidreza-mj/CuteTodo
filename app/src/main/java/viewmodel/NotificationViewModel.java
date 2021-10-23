package viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import java.lang.ref.WeakReference;
import java.util.List;

import hlv.cute.todo.App;
import model.Notification;
import model.Todo;
import repo.dbRepoController.NotificationDBRepository;
import scheduler.alarm.AlarmUtil;

public class NotificationViewModel extends ViewModel {

    private final NotificationDBRepository dbRepository;
    private final WeakReference<Context> context;

    public NotificationViewModel() {
        dbRepository = new NotificationDBRepository();
        context = new WeakReference<>(App.get().getApplicationContext());
    }

    public void addNotification(Todo todo) {
        try {
            if (todo.getArriveDate() < System.currentTimeMillis())
                return;

            Notification notification = new Notification();
            notification.initWith(todo);
            dbRepository.addNotification(notification);

            AlarmUtil.with(context.get()).setAlarm(todo.getId(), todo.getTitle(), todo.getArriveDate());
        } catch (InterruptedException ignored) {
        }
    }

    public void editNotification(Todo todo) {
        try {
            //cancel and delete old alarm
            Notification notification = new Notification();
            notification.initWith(todo);
            AlarmUtil.with(context.get()).cancelAlarm(notification.getId());

            if (todo.getArriveDate() < System.currentTimeMillis()) {
                deleteNotification(notification);
                return;
            }

            dbRepository.editNotification(notification);

            AlarmUtil.with(context.get()).setAlarm(todo.getId(), todo.getTitle(), todo.getArriveDate());
        } catch (InterruptedException ignored) {
        }
    }

    private void deleteNotification(Notification notification) {
        try {
            dbRepository.deleteNotification(notification);
        } catch (InterruptedException ignored) {
        }
    }

    private List<Notification> getAllNotifications() {
        try {
            return dbRepository.getAllNotifications();
        } catch (InterruptedException e) {
            return null;
        }
    }

    private List<Notification> getAllDoneNotifications() {
        try {
            return dbRepository.getAllDoneNotifications();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public Notification getNotification(int id) {
        try {
            return dbRepository.getNotification(id);
        } catch (InterruptedException e) {
            return null;
        }
    }

    private boolean existsNotification(int id) {
        return getNotification(id) != null;
    }

    public void setNotificationEditMode(Todo todo) {
        if (existsNotification(todo.getId()))
            editNotification(todo);
        else
            addNotification(todo);
    }

    private void deleteAllNotifications() {
        try {
            dbRepository.deleteAllNotifications();
        } catch (InterruptedException ignored) {
        }
    }

    private void deleteAllDoneNotifications() {
        try {
            dbRepository.deleteAllDoneNotifications();
        } catch (InterruptedException ignored) {
        }
    }

    public void cancelAllDoneAlarm() {
        List<Notification> notificationList = getAllDoneNotifications();
        if (notificationList != null) {
            for (Notification notification : notificationList)
                AlarmUtil.with(context.get()).cancelAlarm(notification.getId());
        }

        deleteAllDoneNotifications();
    }

    public void cancelAllAlarm() {
        List<Notification> notificationList = getAllNotifications();
        if (notificationList != null) {
            for (Notification notification : notificationList)
                AlarmUtil.with(context.get()).cancelAlarm(notification.getId());
        }

        deleteAllNotifications();
    }

    public void cancelAlarm(Todo todo) {
        Notification notification = new Notification();
        notification.initWith(todo);
        AlarmUtil.with(context.get()).cancelAlarm(notification.getId());
        deleteNotification(notification);
    }
}
