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
            String content = getString(R.string.todo_title) + " " + getTodo().getTitle() + "\n\n";

            if (getTodo().getCategory() != null)
                content += getString(R.string.todo_category) + " " + getTodo().getCategory() + "\n\n";

            content += getString(R.string.todo_priority) + " " + getCurrentPriority() + "\n\n";

            content += getTodo().isDone() ? "انجام شده ✅" : "انجام نشده ❌️";
            content += "\n\n";

            if (getTodo().getArriveDate() != 0)
                content += "🔔" + getString(R.string.todo_reminder) + " " + getCompleteDate(getTodo().getArriveDate()) + "\n\n";

            content += "\n" + "اپلیکیشن مدیریت کارهای روزانه" + " " + getString(R.string.app_name);

            return content;
        }
        return getString(R.string.app_name);
    }

    public int getLytDateVisibility() {
        return getTodo().getArriveDate() != 0 ? View.VISIBLE : View.GONE;
    }

    public int getCreatedAtVisibility() {
        return getTodo().getCreatedAt() != 0 ? View.VISIBLE : View.GONE;
    }

    public int getUpdatedAtVisibility() {
        return getTodo().getUpdatedAt() != 0 ? View.VISIBLE : View.GONE;
    }

    public boolean hasArriveDate() {
        return getTodo().getArriveDate() != 0;
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
        int hour = dateHelper.getHour();
        int minute = dateHelper.getMinute();

        PersianDateImpl persianDate = new PersianDateImpl();
        persianDate.setDate(timeMillis);

        return persianDate.getPersianDayOfWeekName() +
                "، " + persianDate.getPersianDay() +
                " " + persianDate.getPersianMonthName() +
                " " + persianDate.getPersianYear() +
                "، ساعت " + hour + ":" + minute;
    }

}
