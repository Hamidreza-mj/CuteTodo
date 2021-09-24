package ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentAddEditTodoBinding;
import model.Todo;
import utils.ToastHelper;

public class AddEditTodoFragment extends BaseFragment {

    private FragmentAddEditTodoBinding binding;

    private MaterialButton btnAdd;
    private TextInputEditText edtTitle;
    private ChipGroup chipGP;

    private Todo.Priority priority;

    public AddEditTodoFragment() {
    }

    public static AddEditTodoFragment newInstance() {
        return new AddEditTodoFragment();
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
        handleActions();
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back());
        edtTitle = binding.inpEdtTitle;
        btnAdd = binding.mBtnAdd;
        chipGP = binding.chipGP;
    }

    @SuppressLint("NonConstantResourceId")
    private void handleActions() {
        chipGP.check(R.id.chipLow);
        priority = Todo.Priority.LOW; //set default priority

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


        btnAdd.setOnClickListener(view -> {
            Todo todo = new Todo();
            todo.setTitle(edtTitle.getText().toString().trim());
            todo.setPriority(priority);
            todo.setCategory(null);

            String res = getTodoViewModel().validateTodo(todo);

            if (res == null) {
                getTodoViewModel().addTodo(todo);
                back();
                return;
            }

            ToastHelper.get().toast(res);
        });
    }
}
