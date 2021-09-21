package ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import hlv.cute.todo.databinding.FragmentAddEditCategoryBinding;

import utils.Tags;

public class AddEditCategoryFragment extends BaseFragment {

    private FragmentAddEditCategoryBinding binding;

    private MaterialButton btnAdd;

    public AddEditCategoryFragment() {
    }

    public static AddEditCategoryFragment newInstance() {
        return new AddEditCategoryFragment();
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
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back(Tags.BackStack.ADD_EDIT_CATEGORY));
    }
}
