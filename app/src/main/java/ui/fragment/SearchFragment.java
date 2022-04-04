package ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentSearchBinding;
import model.Category;
import model.Search;
import ui.adapter.TodoAdapter;
import ui.dialog.DeleteDialog;
import ui.dialog.MoreDialog;
import ui.fragment.sheet.SearchModeBottomSheet;
import utils.Constants;
import viewmodel.NotificationViewModel;

public class SearchFragment extends BaseFragment {

    private FragmentSearchBinding binding;

    private NotificationViewModel notificationViewModel;

    private ConstraintLayout toolbar;
    private NestedScrollView nested;
    private RecyclerView rvSearch;

    private TodoAdapter adapter;

    private AppCompatEditText edtSearch;
    private AppCompatImageView imgClear;
    private AppCompatImageView imgFilter;
    private AppCompatImageView vectorImage;
    private TextView txtResult, txtNotes;

    private boolean afterTextChanged = false;

    private Search search;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        search = new Search();
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
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
        handleObserver();
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back());

        toolbar = binding.toolbar;
        nested = binding.nested;
        rvSearch = binding.rvSearch;
        imgClear = binding.aImgClear;
        imgFilter = binding.aImgFilter;
        edtSearch = binding.edtSearch;
        vectorImage = binding.vector;
        txtNotes = binding.txtNotes;
        txtResult = binding.txtResult;

        new Handler().postDelayed(() -> {
            edtSearch.setText("");
            edtSearch.requestFocus();
            showKeyboard();
        }, 500);

        handleTabLayout();

        handleShadowScroll();
    }

    private void handleTabLayout() {
        List<Category> allCategories = getCategoryViewModel().getAllCategories();
        Category categoryAllItem = new Category();
        categoryAllItem.setId(0);
        categoryAllItem.setName("همه");
        allCategories.add(0, categoryAllItem);

        Collections.reverse(allCategories);

        int maxLength = 18;

        for (Category category : allCategories) {
            String categoryName = category.getName();

            if (categoryName.length() > maxLength) {
                String categoryWithEllipsis = categoryName.substring(0, maxLength) + getString(R.string.ellipsis);
                binding.tabLyt.addTab(binding.tabLyt.newTab().setText(categoryWithEllipsis));
            } else {
                binding.tabLyt.addTab(binding.tabLyt.newTab().setText(categoryName));
            }
        }

        int lastTabPos = binding.tabLyt.getTabCount() - 1;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            search.setCategoryId(categoryAllItem.getId());
            binding.tabLyt.selectTab(binding.tabLyt.getTabAt(lastTabPos), true);
        }, 100);

        binding.tabLyt.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabCategoryId = allCategories.get(tab.getPosition()).getId();
                search.setCategoryId(tabCategoryId);
                nested.smoothScrollTo(0, 0, 500);

                if (tab.getPosition() == lastTabPos) {//all
                    getSearchViewModel().fetch();
                } else {
                    String searchText = Objects.requireNonNull(edtSearch.getText()).toString();

                    if (searchText.isEmpty()) {
                        getSearchViewModel().fetch(tabCategoryId);
                    } else {
                        search.setTerm(searchText);
                        getSearchViewModel().fetch(search);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                nested.smoothScrollTo(0, 0, 500);
            }
        });
    }

    private void handleShadowScroll() {
        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            final float dpShadow = getResources().getDimension(R.dimen.toolbar_shadow);

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
                } else if (scrollY > 50) {
                    toolbar.setTranslationZ(dpShadow);
                    toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
                }
            }
        });
    }

    private void handleActions() {
        imgFilter.setOnClickListener(view -> {
            if (search.getTerm() == null || search.getSearchMode() == null) {
                search.setTerm("");
                search.setSearchMode(Search.SearchMode.TODO);
            }

            SearchModeBottomSheet searchModeBottomSheet = SearchModeBottomSheet.newInstance(search);
            searchModeBottomSheet.show(getChildFragmentManager(), null);
            searchModeBottomSheet.setOnCheckChanged(search -> {
                searchModeBottomSheet.disableViews();
                search.setTerm(getSearchViewModel().getCurrentTerm());
                getSearchViewModel().search(search);

                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.tabLyt.getLayoutParams();

                if (search.getSearchMode() == Search.SearchMode.CATEGORY || search.getSearchMode() == Search.SearchMode.BOTH) {
                    binding.tabLyt.setVisibility(View.INVISIBLE);
                    lp.height = (int) getResources().getDimension(R.dimen.heigh_invisible_space);
                } else {
                    binding.tabLyt.setVisibility(View.VISIBLE);
                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }

                binding.tabLyt.setLayoutParams(lp);
                binding.tabLyt.requestLayout();

                searchModeBottomSheet.dismiss();
            });
        });

        imgClear.setOnClickListener(view -> {
            edtSearch.setText("");
            imgClear.setVisibility(View.INVISIBLE);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) {
                    afterTextChanged = true;
                    txtResult.setVisibility(View.VISIBLE);
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    afterTextChanged = false;
                    txtResult.setVisibility(View.GONE);
                    imgClear.setVisibility(View.INVISIBLE);
                }

                search.setTerm(editable.toString().trim());
                search.setSearchMode(getSearchViewModel().getSearchMode());
                getSearchViewModel().search(search);
            }
        });

        handleRecyclerView();
    }

    private void handleRecyclerView() {
        if (getActivity() == null)
            return;

        adapter = new TodoAdapter(getActivity(),
                todoID -> {
                    getTodoViewModel().setDoneTodo(todoID);
                    getSearchViewModel().fetch();
                },

                (todoMenu, sharedEl) -> {
                    MoreDialog moreDialog = new MoreDialog(getActivity());
                    moreDialog.setWithDetail(true);
                    moreDialog.show();

                    moreDialog.setOnClickEdit(() -> {
                        moreDialog.dismiss();

                        Fragment fragment = AddEditTodoFragment.newInstance(todoMenu);
                        fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO);
                        transaction.addToBackStack(Constants.BackStack.ADD_EDIT_TODO);
                        transaction.commit();
                    });

                    moreDialog.setOnClickDetail(() -> {
                        moreDialog.dismiss();

                        Fragment fragment = TodoDetailFragment.newInstance(todoMenu);
                        fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.TODO_DETAIL);
                        transaction.addToBackStack(Constants.BackStack.TODO_DETAIL);
                        transaction.commit();
                    });

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
                            if (todoMenu.getArriveDate() != 0)
                                notificationViewModel.cancelAlarm(todoMenu);

                            getTodoViewModel().deleteTodo(todoMenu);
                            getSearchViewModel().fetch();
                            deleteDialog.dismiss();
                        });
                    });

                }
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvSearch.setLayoutManager(layoutManager);
        rvSearch.setAdapter(adapter);
    }

    private void handleObserver() {
        getSearchViewModel().fetch();

        getSearchViewModel().getTodosLiveData().observe(getViewLifecycleOwner(),
                todos -> {
                    if (todos == null || todos.isEmpty()) {
                        txtResult.setVisibility(View.GONE);
                        nested.setVisibility(View.GONE);
                        rvSearch.setVisibility(View.GONE);

                        txtNotes.setVisibility(View.VISIBLE);


                        if (getTodoViewModel().todosIsEmpty())
                            txtNotes.setText(getString(R.string.todos_empty));
                        else {
                            String term = getSearchViewModel().getCurrentTerm();

                            if (term.isEmpty()) {
                                term = "موردی یافت نشد!";
                                txtNotes.setText(term);
                            } else {
                                txtNotes.setText(Html.fromHtml(getString(R.string.todo_not_found, getSearchViewModel().getTitleTerm(), term)));
                            }
                        }


                        vectorImage.setVisibility(View.VISIBLE);
                    } else {
                        txtNotes.setVisibility(View.GONE);
                        vectorImage.setVisibility(View.GONE);
                        txtResult.setText(Html.fromHtml(getString(R.string.search_result, todos.size(), getTodoViewModel().getTodosCount(), getSearchViewModel().getTitleTermResult())));
                        txtResult.setVisibility(afterTextChanged ? View.VISIBLE : View.GONE);
                        nested.setVisibility(View.VISIBLE);
                        rvSearch.setVisibility(View.VISIBLE);
                        rvSearch.post(() -> adapter.getDiffer().submitList(todos));
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        getSearchViewModel().release();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSearchViewModel().fetch();
    }
}
