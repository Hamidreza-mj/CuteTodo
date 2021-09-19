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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentHomeBinding;
import model.Todo;
import ui.adapter.TodoAdapter;
import utils.Tags;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;

    private MaterialButton btnAdd;
    private RecyclerView rvTodo;
    private TodoAdapter adapter;

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
        rvTodo = binding.rvTodo;
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

        handleRecyclerView();
    }

    private void handleRecyclerView() {
        if (getActivity() == null)
            return;

        adapter = new TodoAdapter(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTodo.setLayoutManager(layoutManager);
        rvTodo.setAdapter(adapter);

        ArrayList<Todo> list = new ArrayList<>();

        Todo todo1 = new Todo();
        todo1.setId(1);
        todo1.setTitle("آموزش معماری MVVM");
        todo1.setCategory("برنامه نویسی");
        todo1.setPriority(Todo.Priority.NORMAL);
        todo1.setDone(false);

        Todo todo2 = new Todo();
        todo2.setId(2);
        todo2.setTitle("خرید کتاب درسی");
        todo2.setCategory("دانشگاه");
        todo2.setPriority(Todo.Priority.LOW);
        todo2.setDone(true);

        Todo todo3 = new Todo();
        todo3.setId(3);
        todo3.setTitle("نوشتن قرارداد پروژه مشتری");
        todo3.setCategory("کاری");
        todo3.setPriority(Todo.Priority.HIGH);
        todo3.setDone(false);

        Todo todo4 = new Todo();
        todo4.setId(4);
        todo4.setTitle("خرید نان");
        todo4.setCategory("منزل");
        todo4.setPriority(Todo.Priority.HIGH);
        todo4.setDone(false);

        Todo todo5 = new Todo();
        todo5.setId(5);
        todo5.setTitle("خرید میوه");
        todo5.setCategory("منزل");
        todo5.setPriority(Todo.Priority.LOW);
        todo5.setDone(false);

        Todo todo6 = new Todo();
        todo6.setId(6);
        todo6.setTitle("خرید شیر");
        todo6.setCategory("منزل");
        todo6.setPriority(Todo.Priority.NORMAL);
        todo6.setDone(true);

        list.add(todo1);
        list.add(todo2);
        list.add(todo3);
        list.add(todo4);
        list.add(todo5);
        list.add(todo6);


        adapter.getDiffer().submitList(list);
    }
}
