package repo.dbRepoController;

import java.util.List;

import hlv.cute.todo.App;
import model.Notification;
import repo.dao.NotificationDao;

public class NotificationDBRepository {

    private final NotificationDao dao;
    private Notification notification;
    private List<Notification> notificationList;
    private List<Notification> notificationDoneList;

    public NotificationDBRepository() {
        dao = App.get().todoDatabase().getNotificationDao();
    }

    public void addNotification(Notification notification) throws InterruptedException {
        Thread thread = new Thread(() -> dao.create(notification));
        thread.start();
        thread.join();
    }

    public void editNotification(Notification notification) throws InterruptedException {
        Thread thread = new Thread(() -> dao.update(notification));
        thread.start();
        thread.join();
    }

    public void deleteNotification(Notification notification) throws InterruptedException {
        Thread thread = new Thread(() -> dao.delete(notification));
        thread.start();
        thread.join();
    }

    public List<Notification> getAllNotifications() throws InterruptedException {
        Thread thread = new Thread(() -> notificationList = dao.getAllNotifications());
        thread.start();
        thread.join();
        return notificationList;
    }

    public void deleteShownNotifications() throws InterruptedException {
        Thread thread = new Thread(dao::deleteShownNotifications);
        thread.start();
        thread.join();
    }

    public List<Notification> getAllDoneNotifications() throws InterruptedException {
        Thread thread = new Thread(() -> notificationDoneList = dao.getAllDoneNotifications());
        thread.start();
        thread.join();
        return notificationDoneList;
    }

    public void deleteAllNotifications() throws InterruptedException {
        Thread thread = new Thread(dao::deleteAllNotifications);
        thread.start();
        thread.join();
    }

    public void deleteAllDoneNotifications() throws InterruptedException {
        Thread thread = new Thread(dao::deleteAllDoneNotifications);
        thread.start();
        thread.join();
    }

    public Notification getNotification(long id) throws InterruptedException {
        Thread thread = new Thread(() -> notification = dao.getNotification(id));
        thread.start();
        thread.join();
        return notification;
    }

    public void setDoneTodo(long id) throws InterruptedException {
        Thread thread = new Thread(() -> dao.setDoneTodo(id));
        thread.start();
        thread.join();
    }

    public void setShownTodo(long id) throws InterruptedException {
        Thread thread = new Thread(() -> dao.setShownTodo(id));
        thread.start();
        thread.join();
    }

}
