package viewmodel;

import android.view.View;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hlv.cute.todo.R;
import ir.hamsaa.persiandatepicker.date.PersianDateImpl;
import model.DateTime;
import model.Todo;
import utils.DateHelper;
import utils.ResourceUtils;

public class TodoDetailViewModel extends ViewModel {

    private final MutableLiveData<Todo> todoLiveDate;

    public TodoDetailViewModel() {
        todoLiveDate = new MutableLiveData<>();
    }

    public void setTodo(Todo todo) {
        todoLiveDate.setValue(todo);
    }

    public Todo getTodo() {
        return todoLiveDate.getValue();
    }

    public String shareContent() {
        if (getTodo() != null) {
            String content = getString(R.string.todo_title) + "\n" + getTodo().getTitle() + "\n\n";

            if (getTodo().getCategory() != null)
                content += getString(R.string.todo_category) + " " + getTodo().getCategory() + "\n\n";

            content += getString(R.string.todo_priority) + " " + getCurrentPriority() + "\n\n";

            content += getTodo().isDone() ? "انجام شده ✅" : "انجام نشده ❌️";
            content += "\n\n";

            if (getTodo().getArriveDate() != 0)
                content += "🔔" + getString(R.string.todo_reminder) + "\n" + getCompleteDate(getTodo().getArriveDate()) + "\n\n";

            content += "\n" + getString(R.string.app_name) + "\n" + "اپلیکیشن مدیریت کارهای روزانه";

            return content;
        }
        return getString(R.string.app_name);
    }

    public int getLytDateVisibility() {
        return hasArriveDate() ? View.VISIBLE : View.GONE;
    }

    public int getLytCategoryVisibility() {
        return hasCategory() ? View.VISIBLE : View.GONE;
    }

    public int getCreatedAtVisibility() {
        return hasCreatedAt() ? View.VISIBLE : View.GONE;
    }

    public int getUpdatedAtVisibility() {
        return hasUpdatedAt() ? View.VISIBLE : View.GONE;
    }

    public boolean hasArriveDate() {
        return getTodo().getArriveDate() != 0;
    }

    public boolean hasCategory() {
        return getTodo().getCategoryId() != 0 && getTodo().getCategory() != null;
    }

    public String getDoneText() {
        return getTodo().isDone() ? getString(R.string.is_done) : getString(R.string.is_undone);
    }

    public int getImgDoneResource() {
        return getTodo().isDone() ? R.drawable.checked_normal : R.drawable.unchecked_error;
    }

    public boolean hasCreatedAt() {
        return getTodo().getCreatedAt() != 0;
    }

    public boolean hasUpdatedAt() {
        return getTodo().getUpdatedAt() != 0;
    }

    public String getDateReminder() {
        return getDate(getTodo().getArriveDate()).getPersianDate();
    }

    public String getClockReminder() {
        return getDate(getTodo().getArriveDate()).getClock();
    }

    public String getCompleteCreatedAt() {
        return getCompleteDate(getTodo().getCreatedAt());
    }

    public String getCompleteUpdatedAt() {
        return getCompleteDate(getTodo().getUpdatedAt());
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


    public String getString(@StringRes int stringRes) {
        return ResourceUtils.get().getString(stringRes);
    }

    public LiveData<Todo> getTodoLiveDate() {
        return todoLiveDate;
    }

    private String getCurrentPriority() {
        String priority = getString(R.string.low);

        if (getTodo() != null) {
            switch (getTodo().getPriority()) {
                case LOW:
                default:
                    priority = getString(R.string.low);
                    break;

                case NORMAL:
                    priority = getString(R.string.normal);
                    break;

                case HIGH:
                    priority = getString(R.string.high);
                    break;
            }
        }

        return priority;
    }

    private String getCompleteDate(long timeMillis) {
        DateHelper dateHelper = new DateHelper(timeMillis);
        String hour = dateHelper.getHourString();
        String minute = dateHelper.getMinuteString();

        PersianDateImpl persianDate = new PersianDateImpl();
        persianDate.setDate(timeMillis);

        return persianDate.getPersianDayOfWeekName() +
                "، " + persianDate.getPersianDay() +
                " " + persianDate.getPersianMonthName() +
                " " + persianDate.getPersianYear() +
                "، ساعت " + hour + ":" + minute;
    }

}
