package ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import hlv.cute.todo.databinding.FragmentAddTodoBinding;
import hlv.cute.todo.databinding.FragmentHomeBinding;
import utils.Tags;

public class AddEditTodoFragment extends BaseFragment {

    private FragmentAddTodoBinding binding;

    private MaterialButton btnAdd;

    public AddEditTodoFragment() {
    }

    public static AddEditTodoFragment newInstance() {
        return new AddEditTodoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddTodoBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back(Tags.BackStack.ADD_TODO));
    }
}
