package ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import hlv.cute.todo.databinding.FragmentAddEditCategoryBinding;
import model.Category;
import viewmodel.AddEditCategoryViewModel;

public class AddEditCategoryFragment extends BaseFragment {

    private static final String CATEGORY_ARGS = "category-args";

    private FragmentAddEditCategoryBinding binding;

    private AddEditCategoryViewModel viewModel;

    private TextInputLayout inpLytName;
    private TextInputEditText edtName;
    private TextView txtTitle;
    private MaterialButton btnAdd;

    public AddEditCategoryFragment() {
    }

    public static AddEditCategoryFragment newInstance(Category category) {
        AddEditCategoryFragment fragment = new AddEditCategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(CATEGORY_ARGS, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AddEditCategoryViewModel.class);

        if (getArguments() != null && !getArguments().isEmpty()) {
            Category category = (Category) getArguments().getSerializable(CATEGORY_ARGS);

            if (category != null) {
                viewModel.setEditMode(true);
                viewModel.setCategory(category);
            } else {
                viewModel.setEditMode(false);
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditCategoryBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initLogic();
        handleAction();
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back());

        inpLytName = binding.inpName;
        edtName = binding.inpEdtName;
        txtTitle = binding.txtTitle;
        btnAdd = binding.mBtnSave;
    }

    private void initLogic() {
        new Handler().postDelayed(() -> {
            edtName.requestFocus();
            showKeyboard();
        }, 500);

        txtTitle.setText(viewModel.getTitleFragment());
        btnAdd.setText(viewModel.getButtonPrimaryText());

        //for moving cursor to end of line editText
        edtName.setText("");
        edtName.append(viewModel.getEditTextTitle());
    }

    private void handleAction() {
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0)
                    inpLytName.setError(null);
            }
        });

        btnAdd.setOnClickListener(view -> {
            inpLytName.setError(null);

            if (viewModel.isEditMode()) {
                Category editedCategory = viewModel.editCategory(Objects.requireNonNull(edtName.getText()).toString().trim());

                String res = getCategoryViewModel().validateCategory(editedCategory);

                if (res == null) {
                    getCategoryViewModel().editCategory(editedCategory);
                    getTodoViewModel().fetch(); //need to update todos if category was edited
                    getSearchViewModel().fetch();
                    back();
                    return;
                }

                inpLytName.setError(res);
            } else {
                Category category = viewModel.addCategory(Objects.requireNonNull(edtName.getText()).toString().trim());

                String res = getCategoryViewModel().validateCategory(category);

                if (res == null) {
                    getCategoryViewModel().goToTop();
                    getCategoryViewModel().addCategory(category);
                    back();
                    return;
                }

                inpLytName.setError(res);
            }
        });
    }

}
