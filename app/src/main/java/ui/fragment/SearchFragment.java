package ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentCategoriesBinding;
import hlv.cute.todo.databinding.FragmentSearchBinding;
import model.Category;
import model.Todo;
import ui.adapter.CategoryAdapter;
import ui.adapter.TodoAdapter;
import ui.dialog.MoreDialog;
import utils.DisplayUtils;
import utils.Tags;

public class SearchFragment extends BaseFragment {

    private FragmentSearchBinding binding;

    private ConstraintLayout toolbar;
    private NestedScrollView nested;
    private RecyclerView rvSearch;
    private MaterialButton btnSearch;
    private HideBottomViewOnScrollBehavior<FrameLayout> scrollBehavior;

    private TodoAdapter adapter;
    private int scrollYPos = 0;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater);
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

        toolbar = binding.toolbar;
        nested = binding.nested;
        rvSearch = binding.rvSearch;
        btnSearch = binding.mBtnSearch;

        setScrollBehavior();
        handleShadowScroll();
    }

    private void setScrollBehavior() {
      /*  scrollBehavior = new HideBottomViewOnScrollBehavior<>();
        CoordinatorLayout.LayoutParams lp = ((CoordinatorLayout.LayoutParams) frameLytButton.getLayoutParams());
        lp.setBehavior(scrollBehavior);*/
    }

    private void handleShadowScroll() {
//        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            final float dpShadow = DisplayUtils.getDisplay().dpToPx(nested.getContext(), 12);
//
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                scrollYPos = scrollY;
//
//                if (scrollY == 0) {
//                    toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
//                    //toolbar.setTranslationZ(0);
//                } else if (scrollY > 50) {
//                    toolbar.setTranslationZ(dpShadow);
//                    toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
//                }
//            }
//        });
    }

    private void handleActions() {
//        btnAdd.setOnClickListener(view -> {
//            Fragment fragment = AddEditCategoryFragment.newInstance();
//            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
//            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//            transaction.add(R.id.mainContainer, fragment, Tags.FragmentTag.ADD_EDIT_CATEGORY);
//            transaction.addToBackStack(Tags.BackStack.ADD_EDIT_CATEGORY);
//            transaction.commit();
//        });

        handleRecyclerView();
    }

    private void handleRecyclerView() {
        if (getActivity() == null)
            return;

        adapter = getAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvSearch.setLayoutManager(layoutManager);
        rvSearch.setAdapter(adapter);

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
        todo3.setDone(true);

        Todo todo4 = new Todo();
        todo4.setId(4);
        todo4.setTitle("خرید نان");
        todo4.setCategory("منزل");
        todo4.setPriority(Todo.Priority.HIGH);
        todo4.setDone(true);

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
        list.add(todo6);
        list.add(todo6);
        list.add(todo6);
        list.add(todo6);
        list.add(todo6);
        list.add(todo6);
        list.add(todo6);


        adapter.getDiffer().submitList(list);
    }

    @NonNull
    private TodoAdapter getAdapter() {
        return new TodoAdapter(getActivity(),
                todo -> {

                },
                todo -> {
                    MoreDialog moreDialog = new MoreDialog(getActivity());
                    moreDialog.show();
                }
        );
    }

}
