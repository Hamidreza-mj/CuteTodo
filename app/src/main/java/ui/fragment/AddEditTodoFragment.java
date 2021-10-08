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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
import utils.ResourceUtils;
import utils.Tags;
import viewmodel.AddEditTodoViewModel;

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

    private AddEditTodoViewModel viewModel;

    private DateTime tempDateTime = new DateTime();

    private static final String TODO_ARGS = "todo-args";

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

        viewModel = new ViewModelProvider(this).get(AddEditTodoViewModel.class);

        if (getArguments() != null && !getArguments().isEmpty()) {
            Todo todo = (Todo) getArguments().getSerializable(TODO_ARGS);

            if (todo != null) {
                viewModel.setEditMode(true);
                viewModel.setTodo(todo);
            } else {
                viewModel.setEditMode(false);
            }
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
        txtTitle.setText(viewModel.getTitleFragment());
        edtTitle.setText(viewModel.getEditTextTitle());

        btnAdd.setText(viewModel.getButtonPrimaryText());

        viewModel.firstInitDateTime();

        if (viewModel.isEditMode()) {
            @IdRes int chipID;
            switch (viewModel.getTodo().getPriority()) {
                case LOW:
                default:
                    chipID = R.id.chipLow;
                    viewModel.setPriority(Todo.Priority.LOW);
                    break;

                case NORMAL:
                    chipID = R.id.chipNormal;
                    viewModel.setPriority(Todo.Priority.NORMAL);
                    break;

                case HIGH:
                    chipID = R.id.chipHigh;
                    viewModel.setPriority(Todo.Priority.HIGH);
                    break;
            }

            chipGP.check(chipID);
            return;
        }

        //set default priority
        chipGP.check(R.id.chipLow);
        viewModel.setPriority(Todo.Priority.LOW);
    }

    @SuppressLint("NonConstantResourceId")
    private void handleAction() {
        cardCategory.setOnClickListener(view -> {
            List<Category> categories = getCategoryViewModel().getAllCategories();

            DropDownCategoriesDialog dropDown = new DropDownCategoriesDialog(getActivity(), categories);
            dropDown.show();

            dropDown.setOnClickCategory(category -> {
                dropDown.dismiss();
                viewModel.commitCategory(category);
            });

            dropDown.setOnclickManage(() -> {
                viewModel.commitCategory(null);

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
                    viewModel.setPriority(Todo.Priority.LOW);
                    break;

                case R.id.chipNormal:
                    viewModel.setPriority(Todo.Priority.NORMAL);
                    break;

                case R.id.chipHigh:
                    viewModel.setPriority(Todo.Priority.HIGH);
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

        cardReminder.setOnClickListener(view -> handlePickers(getActivity(), viewModel.getOldDateTime().getDate()));

        imgClear.setOnClickListener(view -> viewModel.releaseAll());

        btnAdd.setOnClickListener(view -> {
            inpLytTitle.setError(null);

            if (viewModel.isEditMode()) {
                Todo editedTodo = viewModel.editTodo(Objects.requireNonNull(edtTitle.getText()).toString().trim());

                String res = getTodoViewModel().validateTodo(editedTodo);
                if (res == null) {
                    getTodoViewModel().editTodo(editedTodo);
                    getSearchViewModel().fetch();
                    back();
                    return;
                }

                inpLytTitle.setError(res);
            } else {
                Todo newTodo = viewModel.addTodo(Objects.requireNonNull(edtTitle.getText()).toString().trim());

                String res = getTodoViewModel().validateTodo(newTodo);
                if (res == null) {
                    getTodoViewModel().goToTop();
                    getTodoViewModel().addTodo(newTodo);
                    back();
                    return;
                }

                inpLytTitle.setError(res);
            }
        });
    }

    private void handlePickers(Context context, PersianPickerDate persianDate) {
        tempDateTime = new DateTime();
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
                        DateHelper dateHelper;
                        if (viewModel.isEditMode() && viewModel.getTodo().getArriveDate() != 0) {
                            //edit mode & has date
                            if (viewModel.oldDateTimeIsValid()) {
                                //old date is valid (set before)
                                tempDateTime.setHour(viewModel.getOldDateTime().getHour());
                                tempDateTime.setMinute(viewModel.getOldDateTime().getMinute());
                            } else {
                                //old date invalid
                                if (viewModel.isCleared()) //if clear old date
                                    dateHelper = new DateHelper(System.currentTimeMillis());
                                else //if old date not found and it exists without any change (normal edit mode)
                                    dateHelper = new DateHelper(viewModel.getTodo().getArriveDate());

                                tempDateTime.setHour(dateHelper.getHour());
                                tempDateTime.setMinute(dateHelper.getMinute());
                            }
                        } else {
                            //add mode or hasn't date (in edit only)
                            if (viewModel.oldDateTimeIsValid()) { //old is valid, set with old datas
                                tempDateTime.setHour(viewModel.getOldDateTime().getHour());
                                tempDateTime.setMinute(viewModel.getOldDateTime().getMinute());
                            } else { //old date is invalid set now current time (edit mode or add [add without date])
                                dateHelper = new DateHelper(System.currentTimeMillis());
                                tempDateTime.setHour(dateHelper.getHour());
                                tempDateTime.setMinute(dateHelper.getMinute());
                            }
                        }


                        tempDateTime.setDate(persianPickerDate);

                        TimePickerSheetDialog sheetTimer = new TimePickerSheetDialog(context, tempDateTime);
                        sheetTimer.show();

                        sheetTimer.setOnClickApply(pickedDateTime -> {
                            viewModel.commitDateTime(pickedDateTime);
                            viewModel.commitOldDateTime(pickedDateTime);
                            sheetTimer.dismiss();
                        });

                        sheetTimer.setOnBackClick(date -> handlePickers(context, date));
                    }

                    @Override
                    public void onDismissed() {
                    }
                });

        if (persianDate == null) {
            picker.setInitDate(
                    PersianDatePickerDialog.THIS_YEAR,
                    PersianDatePickerDialog.THIS_MONTH,
                    PersianDatePickerDialog.THIS_DAY
            );

            if (viewModel.isEditMode()) {
                DateHelper dateHelper = new DateHelper(System.currentTimeMillis());
                tempDateTime.setHour(dateHelper.getHour());
                tempDateTime.setMinute(dateHelper.getMinute());
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
        viewModel.getDateTimeLiveData().observe(getViewLifecycleOwner(), changedDateTime -> {
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

        viewModel.getCategoryLiveData().observe(getViewLifecycleOwner(), category -> {
            txtCategory.setText(viewModel.getCategoryTitleText());

            if (viewModel.categoryIsValid())
                txtCategory.setTextColor(ResourceUtils.get().getColor(R.color.black));
            else
                txtCategory.setTextColor(ResourceUtils.get().getColor(R.color.gray));
        });
    }

    private void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, Constants.REQUEST_CODE_ALARM_RECIEVER, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        alarmManager.cancel(pendingIntent);
    }

}
