package repo.dbRepoController;

import hlv.cute.todo.App;
import model.Notification;
import repo.dao.NotificationDao;

public class NotificationDBRepository {

    private final NotificationDao dao;
    private Notification notification;
    private long count = 0;

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

    public void deleteAllNotifications() throws InterruptedException {
        Thread thread = new Thread(dao::deleteAllNotifications);
        thread.start();
        thread.join();
    }

    public Notification getNotification(int id) throws InterruptedException {
        Thread thread = new Thread(() -> notification = dao.getNotification(id));
        thread.start();
        thread.join();
        return notification;
    }

    public long getNotificationsCount() throws InterruptedException {
        Thread thread = new Thread(() -> count = dao.getNotificationsCount());
        thread.start();
        thread.join();
        return count;
    }
}
