package ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentAddEditCategoryBinding;
import model.Category;

public class AddEditCategoryFragment extends BaseFragment {

    private FragmentAddEditCategoryBinding binding;

    private TextInputLayout inpLytName;
    private TextInputEditText edtName;
    private TextView txtTitle;
    private MaterialButton btnAdd;

    private static final String CATEGORY_ARGS = "category-args";

    private Category category;

    private boolean isEditMode = false;

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

        if (getArguments() != null && !getArguments().isEmpty()) {
            category = (Category) getArguments().getSerializable(CATEGORY_ARGS);

            if (category != null)
                isEditMode = true;
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
        handleLogic();
        handleAction();
    }

    private void handleLogic() {
        edtName.requestFocus();
        showKeyboard();

        if (category != null) {
            txtTitle.setText(getString(R.string.edit_category));
            btnAdd.setText(getString(R.string.edit));
            edtName.setText(category.getName());
            return;
        }

        txtTitle.setText(getString(R.string.add_new_category));
        btnAdd.setText(getString(R.string.save));
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

            if (isEditMode) {
                Category editedCategory = new Category();
                editedCategory.setId(category.getId());
                editedCategory.setName(edtName.getText().toString().trim());

                String res = getCategoryViewModel().validateCategory(editedCategory);

                if (res == null) {
                    getCategoryViewModel().editCategory(editedCategory);
                    getTodoViewModel().fetch(); //need to update todos if category was edited
                    back();
                    return;
                }

                inpLytName.setError(res);
            } else {
                category = new Category();

                category.setName(edtName.getText().toString().trim());

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

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back());

        inpLytName = binding.inpName;
        edtName = binding.inpEdtName;
        txtTitle = binding.txtTitle;
        btnAdd = binding.mBtnSave;
    }
}
