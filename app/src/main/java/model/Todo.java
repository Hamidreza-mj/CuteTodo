package model;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.date.PersianDateImpl;
import utils.DateHelper;

@Keep
@Entity(tableName = "todos")
public class Todo implements Comparable<Todo>, Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "category_id")
    private int categoryId;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "priority")
    private Priority priority;

    @ColumnInfo(name = "is_done")
    private boolean isDone;

    @ColumnInfo(name = "arrive_date", defaultValue = "0")
    private long arriveDate;

    @Ignore
    private DateTime dateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public long getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(long arriveDate) {
        this.arriveDate = arriveDate;
    }

    public DateTime getDateTime() {
        dateTime = new DateTime();

        if (arriveDate == 0)
            return dateTime;

        PersianPickerDate persianDate = new PersianDateImpl();
        persianDate.setDate(arriveDate);

        dateTime.setDate(persianDate);

        DateHelper dateHelper = new DateHelper(arriveDate);

        dateTime.setHour(dateHelper.getHour());
        dateTime.setMinute(dateHelper.getMinute());

        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getPersianDate() {
        if (arriveDate == 0)
            return null;

        PersianPickerDate persianDate = new PersianDateImpl();
        persianDate.setDate(arriveDate);
        return persianDate.getPersianLongDate();
    }

    public String getClock() {
        DateHelper dateHelper = new DateHelper(arriveDate);
        return dateHelper.getClock();
    }

    @Override
    public int compareTo(Todo todo) {
        boolean isSame = id == todo.getId() &&
                Objects.equals(title, todo.getTitle()) &&
                Objects.equals(category, todo.category) &&
                categoryId == todo.getCategoryId() &&
                arriveDate == todo.getArriveDate() &&
                Objects.equals(priority, todo.priority) &&
                isDone == todo.isDone();

        if (isSame)
            return 0;

        return 1;
    }

    public enum Priority {
        LOW, NORMAL, HIGH
    }

}
