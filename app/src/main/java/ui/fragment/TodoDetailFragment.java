package ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.text.MessageFormat;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentTodoDetailBinding;
import model.Todo;
import scheduler.alarm.AlarmUtil;
import ui.dialog.DeleteDialog;
import utils.Constants;
import viewmodel.TodoDetailViewModel;

public class TodoDetailFragment extends BaseFragment {

    private static final String TODO_DETAIL_ARGS = "todo-detail-args";

    private FragmentTodoDetailBinding binding;

    private TodoDetailViewModel viewModel;

    private ConstraintLayout toolbar;
    private NestedScrollView nested;
    private MaterialButton btnClose;
    private AppCompatImageView imgDelete, imgEdit, imgShare;

    private TextView txtTitle;
    private TextView txtCategory;
    private TextView txtLowPriority, txtNormalPriority, txtHighPriority;
    private TextView txtDone;
    private AppCompatImageView imgDone;
    private ConstraintLayout lytDate, lytCategory;
    private TextView txtDateReminder, txtClockReminder;
    private TextView txtCreatedAt, txtUpdatedAt;

    public TodoDetailFragment() {
    }

    public static TodoDetailFragment newInstance(Todo todo) {
        TodoDetailFragment fragment = new TodoDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TODO_DETAIL_ARGS, todo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TodoDetailViewModel.class);

        if (getArguments() != null && !getArguments().isEmpty()) {
            Todo todo = (Todo) getArguments().getSerializable(TODO_DETAIL_ARGS);
            viewModel.setTodo(todo);
        } else {
            back();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTodoDetailBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        handleActions();
        handleObserver();
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back());
        imgDelete = binding.aImgDelete;
        imgEdit = binding.aImgEdit;
        imgShare = binding.aImgShare;
        toolbar = binding.toolbar;
        nested = binding.nested;

        txtTitle = binding.txtTodoTitle;
        txtCategory = binding.txtCategory;

        txtLowPriority = binding.txtLowPriority;
        txtNormalPriority = binding.txtNormalPriority;
        txtHighPriority = binding.txtHighPriority;

        txtDone = binding.txtDone;
        imgDone = binding.imgDone;

        lytDate = binding.lytDate;
        lytCategory = binding.lytCategory;

        txtDateReminder = binding.txtDate;
        txtClockReminder = binding.txtClock;

        txtCreatedAt = binding.txtCreatedAt;
        txtUpdatedAt = binding.txtUpdatedAt;

        btnClose = binding.mBtnClose;

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

    private void handleActions() {
        imgDelete.setOnClickListener(view -> {
            DeleteDialog deleteDialog = new DeleteDialog(getActivity());
            deleteDialog.show();

            deleteDialog.setTitle(getString(R.string.delete_todo));
            String todoTitle = viewModel.getTodo().getTitle();
            if (todoTitle != null && todoTitle.trim().length() > 30)
                todoTitle = todoTitle.substring(0, 30).trim() + getString(R.string.ellipsis);

            deleteDialog.setMessage(getString(R.string.delete_todo_message, todoTitle));
            deleteDialog.setOnClickDelete(() -> {
                if (viewModel.hasArriveDate())
                    AlarmUtil.with(requireContext().getApplicationContext()).cancelAlarm(viewModel.getTodo().getId());

                getTodoViewModel().deleteTodo(viewModel.getTodo());
                getTodoViewModel().fetch(); //need to update todos if categories was deleted
                getSearchViewModel().fetch();

                if (getTodoViewModel().todosIsEmpty())
                    getTodoViewModel().goToTop();

                deleteDialog.dismiss();
                back();
            });
        });

        imgEdit.setOnClickListener(v -> {
            Fragment fragment = AddEditTodoFragment.newInstance(viewModel.getTodo());
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO);
            transaction.addToBackStack(Constants.BackStack.ADD_EDIT_TODO);
            transaction.commit();
        });

        imgShare.setOnClickListener(v -> {
            if (getActivity() == null)
                return;

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, viewModel.shareContent());
            getActivity().startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
        });

        btnClose.setOnClickListener(view -> back());

    }

    private void handleObserver() {
        viewModel.getTodoLiveDate().observe(getViewLifecycleOwner(), todo -> {
            if (todo != null) {
                txtTitle.setText(todo.getTitle());

                switch (todo.getPriority()) {
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

                txtDone.setText(viewModel.getDoneText());
                imgDone.setImageResource(viewModel.getImgDoneResource());

                lytDate.setVisibility(viewModel.getLytDateVisibility());
                lytCategory.setVisibility(viewModel.getLytCategoryVisibility());
                txtCreatedAt.setVisibility(viewModel.getCreatedAtVisibility());
                txtUpdatedAt.setVisibility(viewModel.getUpdatedAtVisibility());

                if (viewModel.hasCategory())
                    txtCategory.setText(todo.getCategory());

                if (viewModel.hasArriveDate()) {
                    txtDateReminder.setText(viewModel.getDateReminder());
                    txtClockReminder.setText(viewModel.getClockReminder());
                }

                if (viewModel.hasCreatedAt()) {
                    txtCreatedAt.setText(MessageFormat.format("{0} {1}",
                            getString(R.string.todo_created_at), viewModel.getCompleteCreatedAt()));
                }

                if (viewModel.hasUpdatedAt()) {
                    txtUpdatedAt.setText(MessageFormat.format("{0} {1}",
                            getString(R.string.todo_updated_at), viewModel.getCompleteUpdatedAt()));
                }
            }
        });

    }

    public TodoDetailViewModel getViewModel() {
        return viewModel;
    }
}
