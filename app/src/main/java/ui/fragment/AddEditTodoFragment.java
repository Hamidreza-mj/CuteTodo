package ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentAddEditTodoBinding;
import model.Todo;
import utils.DisplayUtils;

public class AddEditTodoFragment extends BaseFragment {

    private FragmentAddEditTodoBinding binding;

    private MaterialButton btnAdd;
    private TextInputLayout inpLytTitle;
    private TextInputEditText edtTitle;
    private ChipGroup chipGP;
    private ConstraintLayout toolbar;
    private NestedScrollView nested;
    private TextView txtTitle;

    private Todo.Priority priority;

    private static final String TODO_ARGS = "todo-args";

    private Todo todo;

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
        handleLogic();
        handleAction();
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

        handleShadowScroll();
    }

    private void handleShadowScroll() {
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
    private void handleLogic() {
        if (todo != null) {
            txtTitle.setText(getString(R.string.edit_todo));
            btnAdd.setText(getString(R.string.edit));
            edtTitle.setText(todo.getTitle());

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

        txtTitle.setText(getString(R.string.add_new_todo));
        btnAdd.setText(getString(R.string.save));
        chipGP.check(R.id.chipLow);
        priority = Todo.Priority.LOW; //set default priority
    }

    private void handleAction() {
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
                if (editable.length() > 0) {
                    inpLytTitle.setError(null);
                }
            }
        });

        btnAdd.setOnClickListener(view -> {
            inpLytTitle.setError(null);

            if (isEditMode) {
                Todo editedTodo = new Todo();
                editedTodo.setId(todo.getId());
                editedTodo.setTitle(edtTitle.getText().toString().trim());
                editedTodo.setPriority(priority);
                editedTodo.setCategory(null);
                editedTodo.setDone(todo.isDone());

                String res = getTodoViewModel().validateTodo(editedTodo);

                if (res == null) {
                    getTodoViewModel().editTodo(editedTodo);
                    back();
                    return;
                }

                inpLytTitle.setError(res);
            } else {
                todo = new Todo();

                todo.setTitle(edtTitle.getText().toString().trim());
                todo.setPriority(priority);
                todo.setCategory(null);

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

}
