package model;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.date.PersianDateImpl;
import utils.DateHelper;

@Keep
@Entity(tableName = "notifications")
public class Notification implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey()
    private int id;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "is_done")
    private boolean isDone;

    @ColumnInfo(name = "is_shown", defaultValue = "false")
    private boolean isShown;

    @ColumnInfo(name = "arrive_date", defaultValue = "0")
    private long arriveDate;

    @ColumnInfo(name = "priority")
    private Priority priority;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "category_id", defaultValue = "0")
    private int categoryId;

    @Ignore
    private DateTime dateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    public long getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(long arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public String getClock() {
        DateHelper dateHelper = new DateHelper(arriveDate);
        return dateHelper.getClock();
    }

}
