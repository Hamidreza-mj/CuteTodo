package ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import java.util.List;
import java.util.Objects;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentAddEditTodoBinding;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.api.PersianPickerDate;
import ir.hamsaa.persiandatepicker.api.PersianPickerListener;
import model.Category;
import model.Todo;
import scheduler.alarm.AlarmUtil;
import ui.dialog.DropDownCategoriesDialog;
import ui.dialog.TimePickerSheetDialog;
import utils.Constants;
import utils.ResourceUtils;
import utils.ToastHelper;
import viewmodel.AddEditTodoViewModel;

public class AddEditTodoFragment extends BaseFragment {

    private static final String TODO_ARGS = "todo-args";
    private static final String SHARE_MODE_ARGS = "share-mode-args";

    private FragmentAddEditTodoBinding binding;

    private AddEditTodoViewModel viewModel;

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

    public AddEditTodoFragment() {
    }

    public static AddEditTodoFragment newInstance(Todo todo) {
        AddEditTodoFragment fragment = new AddEditTodoFragment();
        Bundle args = new Bundle();
        args.putSerializable(TODO_ARGS, todo);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddEditTodoFragment newInstanceShare(String title) {
        AddEditTodoFragment fragment = new AddEditTodoFragment();
        Bundle args = new Bundle();
        args.putString(SHARE_MODE_ARGS, title);
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
                //edit mode
                viewModel.setEditMode(true);
                viewModel.setShareMode(false);
                viewModel.setTodo(todo);
            } else {
                //add mode (share or normal)
                viewModel.setEditMode(false);

                String shareTitle = getArguments().getString(SHARE_MODE_ARGS);
                if (shareTitle != null && !shareTitle.isEmpty()) {
                    viewModel.setShareMode(true);
                    viewModel.setShareTitle(shareTitle);
                } else {
                    viewModel.setShareMode(false);
                }
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
        binding.aImgBack.setOnClickListener(view -> {
            if (viewModel.isShareMode()) {
                back();
                Fragment homeFragment = HomeFragment.newInstance();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME);
                transaction.commit();
                return;
            }

            back();
        });

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
            final float dpShadow = getResources().getDimension(R.dimen.toolbar_shadow);

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
                transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.CATEGORY);
                transaction.addToBackStack(Constants.BackStack.CATEGORY);
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
                    if (editedTodo.getArriveDate() != 0) {
                        AlarmUtil.get().cancelAlarm(editedTodo.getId());
                        AlarmUtil.get().setAlarm(editedTodo.getId(), editedTodo.getTitle(), editedTodo.getArriveDate());
                    }

                    getTodoViewModel().editTodo(editedTodo);
                    getSearchViewModel().fetch();
                    updateDetail(editedTodo);
                    back();
                    return;
                }

                inpLytTitle.setError(res);
            } else {
                Todo newTodo = viewModel.addTodo(Objects.requireNonNull(edtTitle.getText()).toString().trim());

                String res = getTodoViewModel().validateTodo(newTodo);
                if (res == null) {
                    if (newTodo.getArriveDate() != 0)
                        AlarmUtil.get().setAlarm(newTodo.getId(), newTodo.getTitle(), newTodo.getArriveDate());

                    getTodoViewModel().goToTop();
                    getTodoViewModel().addTodo(newTodo);
                    ToastHelper.get().toast(getString(R.string.todo_added_successfully));
                    back();

                    if (viewModel.isShareMode()) {
                        btnAdd.setEnabled(false);

                        Fragment homeFragment = HomeFragment.newInstance();
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME);
                        transaction.commit();
                    }

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
                        viewModel.configTempDateTime();
                        viewModel.setDateTemp(persianPickerDate);

                        TimePickerSheetDialog sheetTimer = new TimePickerSheetDialog(context, viewModel.getTempDateTime());
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

        //init default values
        int[] date = viewModel.setInitDateValue(persianDate);
        int year = date[0];
        int month = date[1];
        int day = date[2];

        picker.setInitDate(year, month, day);

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

    private void updateDetail(Todo todo) {
        TodoDetailFragment fragment =
                (TodoDetailFragment) getParentFragmentManager().findFragmentByTag(Constants.FragmentTag.TODO_DETAIL);

        if (fragment != null) {
            if (fragment.getViewModel() != null)
                fragment.getViewModel().setTodo(todo);
        }
    }

    public AddEditTodoViewModel getViewModel() {
        return viewModel;
    }
}
