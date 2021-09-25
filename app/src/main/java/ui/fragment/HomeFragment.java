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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.button.MaterialButton;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentHomeBinding;
import ui.adapter.TodoAdapter;
import ui.dialog.DeleteDialog;
import ui.dialog.GlobalMenuDialog;
import ui.dialog.MoreDialog;
import utils.DisplayUtils;
import utils.Tags;
import utils.ToastHelper;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;

    private ConstraintLayout toolbar;
    private AppCompatImageView imgGlobalMenu;
    private AppCompatImageView imgSearch;
    private NestedScrollView nested;
    private RecyclerView rvTodo;
    private FrameLayout frameLytButton;
    private MaterialButton btnAdd;
    private HideBottomViewOnScrollBehavior<FrameLayout> scrollBehavior;

    private TodoAdapter adapter;
    private int scrollYPos = 0;

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
        handleObserver();
    }

    private void initViews() {
        toolbar = binding.toolbar;
        imgGlobalMenu = binding.aImgGlobalMenu;
        imgSearch = binding.aImgSearch;
        nested = binding.nested;
        rvTodo = binding.rvTodo;
        frameLytButton = binding.frameLytButton;
        btnAdd = binding.mBtnAdd;

        setScrollBehavior();
        handleShadowScroll();
    }

    private void setScrollBehavior() {
        scrollBehavior = new HideBottomViewOnScrollBehavior<>();
        CoordinatorLayout.LayoutParams lp = ((CoordinatorLayout.LayoutParams) frameLytButton.getLayoutParams());
        lp.setBehavior(scrollBehavior);
    }

    private void handleShadowScroll() {
        /*rvTodo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            final float dpShadow = DisplayUtils.getDisplay().dpToPx(rvTodo.getContext(), 12);

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                scrollYPos += dy;

                if (scrollYPos == 0) {
                    toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
                    //toolbar.setTranslationZ(0);
                } else if (scrollYPos > 50) {
                    toolbar.setTranslationZ(dpShadow);
                    toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });*/

        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            final float dpShadow = DisplayUtils.getDisplay().dpToPx(nested.getContext(), 12);

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollYPos = scrollY;

                if (scrollY == 0) {
                    toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
                    //toolbar.setTranslationZ(0);
                } else if (scrollY > 50) {
                    toolbar.setTranslationZ(dpShadow);
                    toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
                }
            }
        });
    }

    private void handleActions() {
        imgGlobalMenu.setOnClickListener(view -> {
            GlobalMenuDialog globalMenu = new GlobalMenuDialog(getActivity());
            globalMenu.show();

            globalMenu.setOnClickCategories(() -> {
                Fragment fragment = CategoriesFragment.newInstance();
                fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.add(R.id.mainContainer, fragment, Tags.FragmentTag.CATEGORY);
                transaction.addToBackStack(Tags.BackStack.CATEGORY);
                transaction.commit();

                globalMenu.dismiss();
            });

            globalMenu.setOnClickDeleteAll(() -> {
                globalMenu.dismiss();

                if (getTodoViewModel().todosIsEmpty()) {
                    ToastHelper.get().toast(getString(R.string.todos_is_empty));
                    return;
                }

                DeleteDialog deleteDialog = new DeleteDialog(getActivity());
                deleteDialog.show();

                deleteDialog.setTitle(getString(R.string.delete_all_todos));
                deleteDialog.setMessage(getString(R.string.delete_all_todos_message, getTodoViewModel().getTodosCount()));
                deleteDialog.setOnClickDelete(() -> {
                    getTodoViewModel().deleteAllTodos();
                    scrollBehavior.slideUp(frameLytButton);
                    deleteDialog.dismiss();
                });
            });
        });

        imgSearch.setOnClickListener(view -> {
            Fragment fragment = SearchFragment.newInstance();
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, fragment, Tags.FragmentTag.SEARCH);
            transaction.addToBackStack(Tags.BackStack.SEARCH);
            transaction.commit();
        });

        btnAdd.setOnClickListener(view -> {
            Fragment fragment = AddEditTodoFragment.newInstance();
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, fragment, Tags.FragmentTag.ADD_EDIT_TODO);
            transaction.addToBackStack(Tags.BackStack.ADD_EDIT_TODO);
            transaction.commit();
        });

        handleRecyclerView();
    }

    private void handleRecyclerView() {
        if (getActivity() == null)
            return;

        adapter = new TodoAdapter(getActivity(),
                todoID -> getTodoViewModel().setDoneTodo(todoID),

                todoMenu -> {
                    MoreDialog moreDialog = new MoreDialog(getActivity());
                    moreDialog.show();
                    moreDialog.setOnClickDelete(() -> {
                        moreDialog.dismiss();

                        DeleteDialog deleteDialog = new DeleteDialog(getActivity());
                        deleteDialog.show();
                        deleteDialog.setTitle(getString(R.string.delete_todo));

                        String todoTitle = todoMenu.getTitle();
                        if (todoTitle != null && todoTitle.trim().length() > 60)
                            todoTitle = todoTitle.substring(0, 60).trim();

                        deleteDialog.setMessage(getString(R.string.delete_todo_message, todoTitle));
                        deleteDialog.setOnClickDelete(() -> {
                            getTodoViewModel().deleteTodo(todoMenu);
                            scrollBehavior.slideUp(frameLytButton);
                            deleteDialog.dismiss();
                        });
                    });
                }
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTodo.setLayoutManager(layoutManager);
//        rvTodo.setItemAnimator(new CustomItemAnimator());
        rvTodo.setAdapter(adapter);
    }

    private void handleObserver() {
        getTodoViewModel().fetch();

        getTodoViewModel().getTodosLiveDate().observe(getViewLifecycleOwner(),
                todos -> rvTodo.post(() -> adapter.getDiffer().submitList(todos))
        );

        getScrollToTopLive().observe(getViewLifecycleOwner(), scroll ->
                nested.smoothScrollTo(0, 0)
        );
    }

    public void goToTop() {
        if (nested == null || scrollBehavior == null || frameLytButton == null)
            return;

        nested.smoothScrollTo(0, 0, 800);
        new Handler().postDelayed(() -> scrollBehavior.slideUp(frameLytButton), 500);
    }

    public int getScrollYPos() {
        return scrollYPos;
    }

}
