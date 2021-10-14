package viewmodel;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

import hlv.cute.todo.R;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.date.PersianDateImpl;
import model.Category;
import model.DateTime;
import model.Todo;
import utils.DateHelper;
import utils.ResourceUtils;

public class AddEditTodoViewModel extends ViewModel {

    private final MutableLiveData<Category> categoryLiveData;
    private final MutableLiveData<DateTime> dateTimeLiveData;
    private final MutableLiveData<DateTime> oldDateTimeLiveData;

    private Todo todo;
    private Todo.Priority priority;

    private DateTime tempDateTime = new DateTime();

    private boolean isEditMode = false;
    private boolean isCleared = false;

    public AddEditTodoViewModel() {
        categoryLiveData = new MutableLiveData<>();
        dateTimeLiveData = new MutableLiveData<>();
        oldDateTimeLiveData = new MutableLiveData<>();
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
        handleCategory();
    }

    public Todo getTodo() {
        return todo;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void firstInitDateTime() {
        DateTime firstDateTime;

        if (isEditMode) {
            firstDateTime = todo.getDateTime();
            if (todo.getDateTime() == null) {
                PersianDateImpl persianDate = new PersianDateImpl();
                persianDate.setDate(PersianDatePickerDialog.THIS_YEAR,
                        PersianDatePickerDialog.THIS_MONTH,
                        PersianDatePickerDialog.THIS_DAY);

                firstDateTime.setDate(persianDate);
            }
        } else {
            firstDateTime = new DateTime();
        }

        oldDateTimeLiveData.setValue(firstDateTime);
        dateTimeLiveData.setValue(firstDateTime);
    }

    public boolean oldDateTimeIsValid() {
        return getOldDateTime() != null && getOldDateTime().getDate() != null;
    }

    public void commitCategory(Category category) {
        categoryLiveData.setValue(category);
    }

    public void commitDateTime(DateTime dateTime) {
        if (dateTime == null)
            dateTime = new DateTime();

        dateTimeLiveData.setValue(dateTime);
    }

    public void commitOldDateTime(DateTime dateTime) {
        if (dateTime == null)
            dateTime = new DateTime();

        oldDateTimeLiveData.setValue(dateTime);
    }

    public void releaseAll() {
        isCleared = true;
        commitDateTime(null);
        commitOldDateTime(null);
    }

    public DateTime getTempDateTime() {
        return tempDateTime;
    }

    public void configTempDateTime() {
        tempDateTime = new DateTime(); //release temp for reseting values
        DateHelper dateHelper;

        if (isEditMode && todo.getArriveDate() != 0) {
            //edit mode & has date
            if (oldDateTimeIsValid()) {
                //old date is valid (set before)
                tempDateTime.setHour(getOldDateTime().getHour());
                tempDateTime.setMinute(getOldDateTime().getMinute());
            } else {
                //old date invalid
                if (isCleared) //if clear old date
                    dateHelper = new DateHelper(System.currentTimeMillis());
                else //if old date not found and it exists without any change (normal edit mode)
                    dateHelper = new DateHelper(todo.getArriveDate());

                tempDateTime.setHour(dateHelper.getHour());
                tempDateTime.setMinute(dateHelper.getMinute());
            }
        } else {
            //add mode or hasn't date (in edit only)
            if (oldDateTimeIsValid()) { //old is valid, set with old datas
                tempDateTime.setHour(getOldDateTime().getHour());
                tempDateTime.setMinute(getOldDateTime().getMinute());
            } else { //old date is invalid set now current time (edit mode or add [add without date])
                dateHelper = new DateHelper(System.currentTimeMillis());
                tempDateTime.setHour(dateHelper.getHour());
                tempDateTime.setMinute(dateHelper.getMinute());
            }
        }
    }

    public void setDateTemp(PersianPickerDate date) {
        tempDateTime.setDate(date);
    }

    private void setHourTemp(int hour) {
        tempDateTime.setHour(hour);
    }

    private void setMinuteTemp(int minute) {
        tempDateTime.setMinute(minute);
    }

    /**
     * set init date value for DatePicker & TimePicker
     *
     * @param persianDate PersianPickerDate
     * @return int[] of date: <br> index[0] -> year <br> index[1] -> month <br> index[2] -> day
     */
    public int[] setInitDateValue(PersianPickerDate persianDate) {
        int[] initValues;
        if (persianDate == null) {
            initValues = new int[]{
                    PersianDatePickerDialog.THIS_YEAR,
                    PersianDatePickerDialog.THIS_MONTH,
                    PersianDatePickerDialog.THIS_DAY
            };

            initTimeEditModeWithUnsetDate();
        } else {
            initValues = new int[]{
                    persianDate.getPersianYear(),
                    persianDate.getPersianMonth(),
                    persianDate.getPersianDay()
            };
        }

        return initValues;
    }

    private void initTimeEditModeWithUnsetDate() {
        //if is edit mode and date not set (hasn't date)
        if (isEditMode) {
            DateHelper dateHelper = new DateHelper(System.currentTimeMillis());
            setHourTemp(dateHelper.getHour());
            setMinuteTemp(dateHelper.getMinute());
        }
    }

    public boolean categoryIsValid() {
        return getCategory() != null && getCategory().getId() != 0 && getCategory().getName() != null;
    }

    public void setPriority(Todo.Priority priority) {
        this.priority = priority;
    }

    public Category getCategory() {
        return categoryLiveData.getValue();
    }

    public DateTime getDateTime() {
        return dateTimeLiveData.getValue();
    }

    public DateTime getOldDateTime() {
        return oldDateTimeLiveData.getValue();
    }

    public Todo addTodo(String title) {
        todo = new Todo();

        todo.setTitle(title);
        todo.setPriority(priority);
        todo.setCreatedAt(System.currentTimeMillis());

        if (getDateTime() != null && getDateTime().getDate() != null) {
            todo.setDateTime(getDateTime());
            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(getDateTime().getDate().getTimestamp());

            calendar.set(Calendar.HOUR_OF_DAY, getDateTime().getHour()); //HOUR_OF_DAY is 24 hours format
            calendar.set(Calendar.MINUTE, getDateTime().getMinute());
            calendar.set(Calendar.SECOND, 0);

            todo.setArriveDate(calendar.getTimeInMillis());
        }

        if (getCategory() != null) {
            todo.setCategoryId(getCategory().getId());
            todo.setCategory(getCategory().getName());
        } else {
            todo.setCategoryId(0);
            todo.setCategory(null);
        }

        return todo;
    }

    public Todo editTodo(String newTitle) {
        Todo mustBeEditTodo = new Todo();
        mustBeEditTodo.setId(todo.getId());
        mustBeEditTodo.setTitle(newTitle);
        mustBeEditTodo.setPriority(priority);
        mustBeEditTodo.setDone(todo.isDone());
        mustBeEditTodo.setCreatedAt(todo.getCreatedAt());
        mustBeEditTodo.setUpdatedAt(System.currentTimeMillis());

        if (getDateTime() != null && getDateTime().getDate() != null) {
            mustBeEditTodo.setDateTime(getDateTime());
            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(getDateTime().getDate().getTimestamp());

            calendar.set(Calendar.HOUR_OF_DAY, getDateTime().getHour()); //HOUR_OF_DAY is 24 hours format
            calendar.set(Calendar.MINUTE, getDateTime().getMinute());
            calendar.set(Calendar.SECOND, 0);

            mustBeEditTodo.setArriveDate(calendar.getTimeInMillis());
        } else {
            mustBeEditTodo.setArriveDate(0);
        }

        if (getCategory() != null) {
            mustBeEditTodo.setCategoryId(getCategory().getId());
            mustBeEditTodo.setCategory(getCategory().getName());
        } else {
            mustBeEditTodo.setCategoryId(0);
            mustBeEditTodo.setCategory(null);
        }

        return mustBeEditTodo;
    }

    //region: get text and strings
    public String getTitleFragment() {
        return getString(isEditMode ? R.string.edit_todo : R.string.add_new_todo);
    }

    public String getButtonPrimaryText() {
        return getString(isEditMode ? R.string.edit : R.string.save);
    }

    public String getEditTextTitle() {
        return isEditMode ? todo.getTitle() : "";
    }

    public String getCategoryTitleText() {
        return categoryIsValid() ? getCategory().getName() : getString(R.string.enter_category_name);
    }
    //endregion

    public LiveData<Category> getCategoryLiveData() {
        return categoryLiveData;
    }

    public LiveData<DateTime> getDateTimeLiveData() {
        return dateTimeLiveData;
    }

    private String getString(@StringRes int stringRes) {
        return ResourceUtils.get().getString(stringRes);
    }

    private void handleCategory() {
        if (todo.getCategoryId() != 0 && todo.getCategory() != null) {
            Category category = new Category();
            category.setId(todo.getCategoryId());
            category.setName(todo.getCategory());
            commitCategory(category);
        }
    }

}
