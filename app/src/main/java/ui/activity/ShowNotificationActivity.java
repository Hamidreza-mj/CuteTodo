package ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.ActivityShowNotificationBinding;
import model.Notification;
import utils.ToastHelper;
import viewmodel.ShowNotificationViewModel;

public class ShowNotificationActivity extends BaseActivity {

    private ActivityShowNotificationBinding binding;

    private ShowNotificationViewModel viewModel;

    private ConstraintLayout toolbar;
    private NestedScrollView nested;
    private MaterialButton btnDone;

    private TextView txtTitle;
    private TextView txtCategory;
    private TextView txtLowPriority, txtNormalPriority, txtHighPriority;
    private ConstraintLayout lytDate, lytCategory;
    private TextView txtDateReminder, txtClockReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViewModel();
        handleObserver();
        initViews();
        handleLogic();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ShowNotificationViewModel.class);
        viewModel.setIntent(getIntent());
    }

    private void handleObserver() {
        viewModel.getCloseLive().observe(this, mustClose -> {
            if (mustClose != null && mustClose)
                close();
        });
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(v -> close());
        binding.mBtnClose.setOnClickListener(v -> close());

        toolbar = binding.toolbar;
        nested = binding.nested;

        txtTitle = binding.txtTodoTitle;
        txtCategory = binding.txtCategory;

        txtLowPriority = binding.txtLowPriority;
        txtNormalPriority = binding.txtNormalPriority;
        txtHighPriority = binding.txtHighPriority;

        lytDate = binding.lytDate;
        lytCategory = binding.lytCategory;

        txtDateReminder = binding.txtDate;
        txtClockReminder = binding.txtClock;

        btnDone = binding.mBtnDone;
        handleShadowScroll();
    }

    private void handleShadowScroll() {
        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            final float dpShadow = getResources().getDimension(R.dimen.toolbar_shadow);

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
                } else if (scrollY > 50) {
                    toolbar.setTranslationZ(dpShadow);
                    toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
                }
            }
        });
    }

    private void handleLogic() {
        Notification notif = viewModel.getNotification();
        if (notif == null) { //when manual close and open with home
            runActivity(MainActivity.class, true);
            return;
        }

        //todo: set shown true in startup get all is shown and delete it

        txtTitle.setText(notif.getContent());

        switch (notif.getPriority()) {
            case LOW:
            default:
                txtLowPriority.setVisibility(View.VISIBLE);
                txtNormalPriority.setVisibility(View.GONE);
                txtHighPriority.setVisibility(View.GONE);
                break;

            case NORMAL:
                txtLowPriority.setVisibility(View.GONE);
                txtNormalPriority.setVisibility(View.VISIBLE);
                txtHighPriority.setVisibility(View.GONE);
                break;

            case HIGH:
                txtLowPriority.setVisibility(View.GONE);
                txtNormalPriority.setVisibility(View.GONE);
                txtHighPriority.setVisibility(View.VISIBLE);
                break;
        }

        lytDate.setVisibility(viewModel.getLytDateVisibility());
        lytCategory.setVisibility(viewModel.getLytCategoryVisibility());

        if (viewModel.hasCategory())
            txtCategory.setText(notif.getCategory());

        if (viewModel.hasArriveDate()) {
            txtDateReminder.setText(viewModel.getDateReminder());
            txtClockReminder.setText(viewModel.getClockReminder());
        }


        btnDone.setOnClickListener(v -> {
            viewModel.done();
            ToastHelper.get().toast(getString(R.string.todo_done_successfully));
            new Handler().postDelayed(this::close, 500);
        });
    }

    private void close() {
        viewModel.deleteNotification();
        finish();
    }

    @Override
    public void onBackPressed() {
        close();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
    }
}