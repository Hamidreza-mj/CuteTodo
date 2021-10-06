package ui.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentAddEditTodoBinding;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import model.Category;
import model.DateTime;
import model.Todo;
import scheduler.receiver.AlarmReceiver;
import ui.dialog.DropDownCategoriesDialog;
import ui.dialog.TimePickerSheetDialog;
import utils.Constants;
import utils.DateHelper;
import utils.DisplayUtils;
import utils.Tags;

public class AddEditTodoFragment extends BaseFragment {

    private FragmentAddEditTodoBinding binding;

    private MaterialButton btnAdd;
    private TextInputLayout inpLytTitle;
    private TextInputEditText edtTitle;
    private ChipGroup chipGP;
    private ConstraintLayout toolbar;
    private NestedScrollView nested;
    private TextView txtTitle;
    private MaterialCardView cardCategory;
    private MaterialCardView cardReminder;
    private AppCompatImageView imgClear;
    private TextView txtCategory;
    private TextView txtDate;

    private Todo todo;
    private Todo.Priority priority;
    private Category category;
    private DateTime dateTime;

    private MutableLiveData<DateTime> dateTimeLiveData;

    private static final String TODO_ARGS = "todo-args";

    private boolean isEditMode = false;

    public AddEditTodoFragment() {
    }

    public static AddEditTodoFragment newInstance(Todo todo) {
        AddEditTodoFragment fragment = new AddEditTodoFragment();
        Bundle args = new Bundle();
        args.putSerializable(TODO_ARGS, todo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dateTimeLiveData = new MutableLiveData<>();

        if (getArguments() != null && !getArguments().isEmpty()) {
            todo = (Todo) getArguments().getSerializable(TODO_ARGS);

            if (todo != null)
                isEditMode = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditTodoBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initLogic();
        handleAction();
        handleObserver();
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back());
        inpLytTitle = binding.inpTitle;
        edtTitle = binding.inpEdtTitle;
        btnAdd = binding.mBtnAdd;
        chipGP = binding.chipGP;
        toolbar = binding.toolbar;
        nested = binding.nested;
        txtTitle = binding.txtTitle;
        cardCategory = binding.mCardCategory;
        cardReminder = binding.mCardReminder;
        imgClear = binding.aImgClear;
        txtCategory = binding.txtCategory;
        txtDate = binding.txtDate;

        handleScroll();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void handleScroll() {
        edtTitle.setOnTouchListener((v, event) -> {
            if (edtTitle.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });


        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            final float dpShadow = DisplayUtils.getDisplay().dpToPx(nested.getContext(), 12);

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
                    //toolbar.setTranslationZ(0);
                } else if (scrollY > 0) {
                    toolbar.setTranslationZ(dpShadow);
                    toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void initLogic() {
        if (isEditMode) {
            txtTitle.setText(getString(R.string.edit_todo));
            btnAdd.setText(getString(R.string.edit));
            edtTitle.setText(todo.getTitle());

            dateTimeLiveData.setValue(todo.getDateTime());

            if (todo.getCategoryId() != 0 && todo.getCategory() != null) {
                category = new Category();
                category.setId(todo.getCategoryId());
                category.setName(todo.getCategory());

                txtCategory.setText(todo.getCategory());

                if (getActivity() != null)
                    txtCategory.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            }

            @IdRes int chipID;

            switch (todo.getPriority()) {
                case LOW:
                default:
                    chipID = R.id.chipLow;
                    priority = Todo.Priority.LOW;
                    break;

                case NORMAL:
                    chipID = R.id.chipNormal;
                    priority = Todo.Priority.NORMAL;
                    break;

                case HIGH:
                    chipID = R.id.chipHigh;
                    priority = Todo.Priority.HIGH;
                    break;
            }

            chipGP.check(chipID);
            return;
        }

        dateTimeLiveData.setValue(new DateTime()); //dateTime must be not null for using in DatePicker
        txtTitle.setText(getString(R.string.add_new_todo));
        btnAdd.setText(getString(R.string.save));
        chipGP.check(R.id.chipLow);
        priority = Todo.Priority.LOW; //set default priority
    }

    @SuppressLint("NonConstantResourceId")
    private void handleAction() {
        cardCategory.setOnClickListener(view -> {
            List<Category> categories = getCategoryViewModel().getAllCategories();
            DropDownCategoriesDialog dropDown = new DropDownCategoriesDialog(getActivity(), categories);
            dropDown.show();

            dropDown.setOnClickCategory(category -> {
                dropDown.dismiss();

                if (category.getId() == 0 && category.getName() == null) {
                    this.category = null;
                    if (getActivity() != null)
                        txtCategory.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray));
                    txtCategory.setText(R.string.enter_category_name);
                    return;
                }

                this.category = category;

                txtCategory.setText(category.getName());

                if (getActivity() != null)
                    txtCategory.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            });

            dropDown.setOnclickManage(() -> {
                this.category = null;

                if (getActivity() != null)
                    txtCategory.setTextColor(ContextCompat.getColor(getActivity(), R.color.gray));
                txtCategory.setText(R.string.enter_category_name);

                Fragment fragment = CategoriesFragment.newInstance();
                fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.add(R.id.mainContainer, fragment, Tags.FragmentTag.CATEGORY);
                transaction.addToBackStack(Tags.BackStack.CATEGORY);
                transaction.commit();

                dropDown.dismiss();
            });

        });

        chipGP.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.chipLow:
                default:
                    priority = Todo.Priority.LOW;
                    break;

                case R.id.chipNormal:
                    priority = Todo.Priority.NORMAL;
                    break;

                case R.id.chipHigh:
                    priority = Todo.Priority.HIGH;
                    break;
            }
        });

        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    inpLytTitle.setError(null);
            }
        });

        cardReminder.setOnClickListener(view -> handlePickers(getActivity(), dateTime.getDate()));

        imgClear.setOnClickListener(view -> dateTimeLiveData.setValue(new DateTime()));

        btnAdd.setOnClickListener(view -> {
            inpLytTitle.setError(null);

            if (isEditMode) {
                Todo editedTodo = new Todo();
                editedTodo.setId(todo.getId());
                editedTodo.setTitle(edtTitle.getText().toString().trim());
                editedTodo.setPriority(priority);

                if (dateTime != null && dateTime.getDate() != null) {
                    editedTodo.setDateTime(dateTime);
                    Calendar calendar = Calendar.getInstance();

                    calendar.setTimeInMillis(dateTime.getDate().getTimestamp());

                    calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHour()); //HOUR_OF_DAY is 24 hours format
                    calendar.set(Calendar.MINUTE, dateTime.getMinute());
                    calendar.set(Calendar.SECOND, 0);

                    editedTodo.setArriveDate(calendar.getTimeInMillis());
                } else {
                    editedTodo.setArriveDate(0);
                }

                if (category != null) {
                    editedTodo.setCategoryId(category.getId());
                    editedTodo.setCategory(category.getName());
                } else {
                    editedTodo.setCategoryId(0);
                    editedTodo.setCategory(null);
                }

                editedTodo.setDone(todo.isDone());

                String res = getTodoViewModel().validateTodo(editedTodo);

                if (res == null) {
                    getTodoViewModel().editTodo(editedTodo);
                    getSearchViewModel().fetch();
                    back();
                    return;
                }

                inpLytTitle.setError(res);
            } else {
                todo = new Todo();

                todo.setTitle(edtTitle.getText().toString().trim());
                todo.setPriority(priority);

                if (dateTime != null && dateTime.getDate() != null) {
                    todo.setDateTime(dateTime);
                    Calendar calendar = Calendar.getInstance();

                    calendar.setTimeInMillis(dateTime.getDate().getTimestamp());

                    calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHour()); //HOUR_OF_DAY is 24 hours format
                    calendar.set(Calendar.MINUTE, dateTime.getMinute());
                    calendar.set(Calendar.SECOND, 0);

                    todo.setArriveDate(calendar.getTimeInMillis());
                }

                if (category != null) {
                    todo.setCategoryId(category.getId());
                    todo.setCategory(category.getName());
                } else {
                    todo.setCategoryId(0);
                    todo.setCategory(null);
                }


                String res = getTodoViewModel().validateTodo(todo);

                if (res == null) {
                    getTodoViewModel().goToTop();
                    getTodoViewModel().addTodo(todo);
                    back();
                    return;
                }

                inpLytTitle.setError(res);
            }
        });
    }

    private void handlePickers(Context context, PersianPickerDate persianDate) {
        PersianDatePickerDialog picker = new PersianDatePickerDialog(context)
                .setPositiveButtonString("مرحله بعد")
                .setNegativeButton("انصراف")
                .setTodayButton("تاریخ امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1400)
                .setMaxYear(1440)
                .setTypeFace(Typeface.createFromAsset(context.getAssets(), "font/vazir_medium.ttf"))
                .setTitleColor(ContextCompat.getColor(context, R.color.blue))
                .setAllButtonsTextSize(15)
                .setPickerBackgroundColor(ContextCompat.getColor(context, R.color.white))
                .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
                .setShowInBottomSheet(true)
                .setListener(new PersianPickerListener() {
                    @Override
                    public void onDateSelected(@NotNull PersianPickerDate persianPickerDate) {
                        /*Log.d("TAG", "onDateSelected: " + persianPickerDate.getTimestamp());//675930448000
                        Log.d("TAG", "onDateSelected: " + persianPickerDate.getGregorianDate());//Mon Jun 03 10:57:28 GMT+04:30 1991
                        Log.d("TAG", "onDateSelected: " + persianPickerDate.getPersianLongDate());// دوشنبه  13  خرداد  1370
                        Log.d("TAG", "onDateSelected: " + persianPickerDate.getPersianMonthName());//خرداد
                        Log.d("TAG", "onDateSelected: " + PersianCalendarUtils.isPersianLeapYear(persianPickerDate.getPersianYear()));//true
                        Toast.makeText(context, persianPickerDate.getPersianYear() + "/" + persianPickerDate.getPersianMonth() + "/" + persianPickerDate.getPersianDay(), Toast.LENGTH_SHORT).show();*/

                        dateTime.setDate(persianPickerDate);

                        //set default value for clock in TimePicker in add mode
                        if (!isEditMode) {
                            DateHelper dateHelper = new DateHelper(System.currentTimeMillis());
                            dateTime.setHour(dateHelper.getHour());
                            dateTime.setMinute(dateHelper.getMinute());
                        }

                        TimePickerSheetDialog sheetTimer = new TimePickerSheetDialog(context, dateTime);
                        sheetTimer.setOnClickApply(pickedDateTime -> {
                            dateTimeLiveData.setValue(pickedDateTime);
                            sheetTimer.dismiss();
                        });

                        sheetTimer.setOnBackClick(date -> handlePickers(context, date));
                        sheetTimer.show();
                    }

                    @Override
                    public void onDismissed() {
                        /*PersianDateImpl currentDate = new PersianDateImpl();
                        if (isEditMode) {
                            if (todo.getArriveDate() != 0) {
                                currentDate.setDate(todo.getArriveDate());
                            } else {
                                currentDate.setDate(
                                        PersianDatePickerDialog.THIS_YEAR,
                                        PersianDatePickerDialog.THIS_MONTH,
                                        PersianDatePickerDialog.THIS_DAY
                                );
                            }
                        } else {
                            currentDate.setDate(
                                    PersianDatePickerDialog.THIS_YEAR,
                                    PersianDatePickerDialog.THIS_MONTH,
                                    PersianDatePickerDialog.THIS_DAY
                            );

                        }
                        dateTime.setDate(currentDate);*/
                    }
                });

        if (persianDate == null) {
            picker.setInitDate(
                    PersianDatePickerDialog.THIS_YEAR,
                    PersianDatePickerDialog.THIS_MONTH,
                    PersianDatePickerDialog.THIS_DAY
            );

            //set default value for clock in TimePicker in edit mode
            if (isEditMode) {
                DateHelper dateHelper = new DateHelper(System.currentTimeMillis());
                dateTime.setHour(dateHelper.getHour());
                dateTime.setMinute(dateHelper.getMinute());
            }
        } else {
            picker.setInitDate(
                    persianDate.getPersianYear(),
                    persianDate.getPersianMonth(),
                    persianDate.getPersianDay()
            );
        }

        picker.show();

    }

    private void handleObserver() {
        dateTimeLiveData.observe(getViewLifecycleOwner(), changedDateTime -> {
            dateTime = changedDateTime;

            if (changedDateTime != null && changedDateTime.getDate() != null) {
                txtDate.setTextColor(ContextCompat.getColor(txtDate.getContext(), R.color.black));
                txtDate.setText(MessageFormat.format("{0}\nساعت {1}",
                        changedDateTime.getPersianDate(),
                        changedDateTime.getClock())
                );
                imgClear.setVisibility(View.VISIBLE);
            } else {
                txtDate.setTextColor(ContextCompat.getColor(txtDate.getContext(), R.color.gray));
                txtDate.setText(getString(R.string.set_date_time));
                imgClear.setVisibility(View.GONE);
            }

        });
    }

    private void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, Constants.REQUEST_CODE_ALARM_RECIEVER, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        alarmManager.cancel(pendingIntent);
    }

}
