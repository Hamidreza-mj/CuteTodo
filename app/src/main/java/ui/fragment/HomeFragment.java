package ui.fragment;

import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentHomeBinding;
import utils.Tags;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;

    private MaterialButton btnAdd;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        handleActions();
    }

    private void initViews() {
        btnAdd = binding.mBtnAdd;
    }

    private void handleActions() {
        btnAdd.setOnClickListener(view -> {
            Fragment addFragment = AddEditTodoFragment.newInstance();
            addFragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, addFragment, Tags.FragmentTag.ADD_TODO);
            transaction.addToBackStack(Tags.BackStack.ADD_TODO);
            transaction.commit();
        });
    }
}
