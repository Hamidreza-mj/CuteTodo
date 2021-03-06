package ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentHomeBinding;
import model.Filter;
import ui.adapter.TodoAdapter;
import ui.dialog.DeleteDialog;
import ui.dialog.GlobalMenuDialog;
import ui.dialog.MoreDialog;
import ui.fragment.sheet.FilterBottomSheet;
import utils.Constants;
import utils.ToastHelper;
import viewmodel.NotificationViewModel;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;

    private NotificationViewModel notificationViewModel;

    private ConstraintLayout toolbar;
    private AppCompatImageView imgGlobalMenu;
    private AppCompatImageView imgFilter;
    private View filterIndicator;
    private AppCompatImageView imgSearch;
    private NestedScrollView nested;
    private RecyclerView rvTodo;
    private FrameLayout frameLytButton;
    private MaterialButton btnAdd;
    private ConstraintLayout lytEmpty;
    private ConstraintLayout lytGuide;
    private TextView txtEmptyTitle;
    private TextView txtEmptyNotes;
    private HideBottomViewOnScrollBehavior<FrameLayout> scrollBehavior;

    private TodoAdapter adapter;
    private int scrollYPos = 0;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
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
        filterIndicator = binding.filterIndicator;
        imgFilter = binding.aImgFilter;
        imgSearch = binding.aImgSearch;
        nested = binding.nested;
        rvTodo = binding.rvTodo;
        frameLytButton = binding.frameLytButton;
        btnAdd = binding.mBtnAdd;
        lytEmpty = binding.cLytEmpty;
        lytGuide = binding.cLytGuide;
        txtEmptyTitle = binding.txtEmpty;
        txtEmptyNotes = binding.txtNotesEmpty;

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
            final float dpShadow = getResources().getDimension(R.dimen.toolbar_shadow);

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
                transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.CATEGORY);
                transaction.addToBackStack(Constants.FragmentTag.CATEGORY);
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
                    notificationViewModel.cancelAllAlarm();
                    getTodoViewModel().deleteAllTodos();
                    scrollBehavior.slideUp(frameLytButton);
                    deleteDialog.dismiss();
                });
            });

            globalMenu.setOnClickDeleteAllDoneTodos(() -> {
                globalMenu.dismiss();

                if (getTodoViewModel().todosIsEmpty()) {
                    ToastHelper.get().toast(getString(R.string.todos_is_empty));
                    return;
                }

                if (getTodoViewModel().todosDoneIsEmpty()) {
                    ToastHelper.get().toast(getString(R.string.todos_done_is_empty));
                    return;
                }

                DeleteDialog deleteDialog = new DeleteDialog(getActivity());
                deleteDialog.show();

                deleteDialog.setTitle(getString(R.string.delete_all_done_todos));
                deleteDialog.setMessage(getString(R.string.delete_all_done_todos_message, getTodoViewModel().getDoneTodosCount()));
                deleteDialog.setOnClickDelete(() -> {
                    notificationViewModel.cancelAllDoneAlarm();
                    getTodoViewModel().deleteAllDoneTodos();
                    scrollBehavior.slideUp(frameLytButton);
                    deleteDialog.dismiss();
                });
            });
        });

        imgFilter.setOnClickListener(view -> {
            FilterBottomSheet filterBottomSheet = FilterBottomSheet.newInstance(getTodoViewModel().getCurrentFilter(), new ArrayList<>(getCategoryViewModel().getAllCategories()));
            filterBottomSheet.show(getChildFragmentManager(), null);

            filterBottomSheet.setOnApplyClick(() -> {
                filterBottomSheet.disableViews();
                Filter filter = filterBottomSheet.getFilter();

//              if (!filter.filterIsEmpty() || getTodoViewModel().getTodosCount() != 0) //if all filter is empty do nothing
                getTodoViewModel().applyFilter(filter);
                //goToTop(800);
                scrollBehavior.slideUp(frameLytButton);
                filterBottomSheet.dismiss();
            });

            filterBottomSheet.setOnClearClick(() -> {
                filterBottomSheet.clearFilterViews();
                getTodoViewModel().applyFilter(null);
                //goToTop(800);
                scrollBehavior.slideUp(frameLytButton);
                filterBottomSheet.dismiss();
            });
        });

        imgSearch.setOnClickListener(view -> {
            Fragment fragment = SearchFragment.newInstance();
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.SEARCH);
            transaction.addToBackStack(Constants.FragmentTag.SEARCH);
            transaction.commit();
        });

        btnAdd.setOnClickListener(view -> {
            Fragment fragment = AddEditTodoFragment.newInstance(null);
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO);
            transaction.addToBackStack(Constants.FragmentTag.ADD_EDIT_TODO);
            transaction.commit();
        });

        handleRecyclerView();
    }

    private void handleRecyclerView() {
        if (getActivity() == null)
            return;

        adapter = new TodoAdapter(getActivity(),
                todoID -> getTodoViewModel().setDoneTodo(todoID),

                (todoMenu, sharedElement) -> {
                    MoreDialog moreDialog = new MoreDialog(getActivity());
                    moreDialog.setWithDetail(true);
                    moreDialog.show();

                    moreDialog.setOnClickEdit(() -> {
                        moreDialog.dismiss();

                        Fragment fragment = AddEditTodoFragment.newInstance(todoMenu);
                        fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO);
                        transaction.addToBackStack(Constants.FragmentTag.ADD_EDIT_TODO);
                        transaction.commit();
                    });

                    moreDialog.setOnClickDetail(() -> {
                        moreDialog.dismiss();

                        Fragment fragment = TodoDetailFragment.newInstance(todoMenu);
                        fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
//                        MaterialContainerTransform t = new MaterialContainerTransform();
//                        t.setDuration(400);
//                        t.setScrimColor(Color.TRANSPARENT);
//                        t.setPathMotion(new MaterialArcMotion());
//                        t.setStartElevation(0);
//                        t.setEndElevation(0);
//
//                        fragment.setSharedElementEnterTransition(t);
//                        fragment.setSharedElementReturnTransition(t);

//                        fragment.setAllowEnterTransitionOverlap(true);
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                        transaction.setReorderingAllowed(true);
//                        scheduleStartPostponedTransition(sharedElement);
//                        transaction.addSharedElement(sharedElement, sharedElement.getTransitionName());
                        transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.TODO_DETAIL);
//                        transaction.hide(this);
                        transaction.addToBackStack(Constants.FragmentTag.TODO_DETAIL);
                        transaction.commit();
                    });

                    moreDialog.setOnClickDelete(() -> {
                        moreDialog.dismiss();

                        DeleteDialog deleteDialog = new DeleteDialog(getActivity());
                        deleteDialog.show();
                        deleteDialog.setTitle(getString(R.string.delete_todo));

                        String todoTitle = todoMenu.getTitle();
                        if (todoTitle != null && todoTitle.trim().length() > 30)
                            todoTitle = todoTitle.substring(0, 30).trim() + getString(R.string.ellipsis);

                        deleteDialog.setMessage(getString(R.string.delete_todo_message, todoTitle));
                        deleteDialog.setOnClickDelete(() -> {
                            if (todoMenu.getArriveDate() != 0)
                                notificationViewModel.cancelAlarm(todoMenu);

                            getTodoViewModel().deleteTodo(todoMenu);
                            scrollBehavior.slideUp(frameLytButton);
                            deleteDialog.dismiss();
                        });
                    });


                }
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvTodo.setLayoutManager(layoutManager);
        rvTodo.setAdapter(adapter);
    }

    private void handleObserver() {
        AppCompatImageView box = binding.box;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) box.getLayoutParams();

        getTodoViewModel().fetch();

        getTodoViewModel().getTodosLiveData().observe(getViewLifecycleOwner(),
                todos -> {
                    if (todos == null || todos.isEmpty()) {
                        rvTodo.setVisibility(View.GONE);
                        lytEmpty.setVisibility(View.VISIBLE);

                        if (getTodoViewModel().getCurrentFilter() == null /*|| getTodoViewModel().getCurrentFilter().filterIsEmpty()*/) {
                            filterIndicator.setVisibility(View.GONE);
                            lytGuide.setVisibility(View.VISIBLE);
                            txtEmptyTitle.setText(getString(R.string.todos_empty));
                            txtEmptyNotes.setText(Html.fromHtml(getString(R.string.todos_empty_notes)));
                            params.verticalBias = 0.2f;
                        } else {
                            filterIndicator.setVisibility(View.VISIBLE);
                            lytGuide.setVisibility(View.GONE);
                            txtEmptyTitle.setText(getString(R.string.empty_todos_with_filter));
                            txtEmptyNotes.setText(getString(R.string.empty_todos_with_filter_notes));
                            params.verticalBias = 0.35f;
                        }

                        box.setLayoutParams(params);

                    } else {
                        filterIndicator.setVisibility(getTodoViewModel().getCurrentFilter() != null &&
                                !getTodoViewModel().getCurrentFilter().filterIsEmpty() ? View.VISIBLE : View.GONE);

                        lytEmpty.setVisibility(View.GONE);
                        rvTodo.setVisibility(View.VISIBLE);
                        rvTodo.post(() -> adapter.getDiffer().submitList(todos));
                    }
                }
        );

        getTodoViewModel().getFilterLiveData().observe(getViewLifecycleOwner(),
                filter -> getTodoViewModel().fetch(filter));

        getTodoViewModel().getGoToTopLiveData().observe(getViewLifecycleOwner(), scroll ->
                goToTop(1000)
        );
    }

    public void goToTop(int duration) {
        if (nested == null || scrollBehavior == null || frameLytButton == null)
            return;

        nested.smoothScrollTo(0, 0, duration);
        new Handler().postDelayed(() -> scrollBehavior.slideUp(frameLytButton), 500);
    }

    public int getScrollYPos() {
        return scrollYPos;
    }

    @Override
    public void onResume() {
        super.onResume();
        getTodoViewModel().fetch();
    }
}
