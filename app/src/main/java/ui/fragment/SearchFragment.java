package ui.fragment;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentSearchBinding;
import model.Search;
import ui.adapter.TodoAdapter;
import ui.dialog.DeleteDialog;
import ui.dialog.MoreDialog;
import ui.fragment.sheet.SearchModeBottomSheet;
import utils.DisplayUtils;
import utils.Tags;

public class SearchFragment extends BaseFragment {

    private FragmentSearchBinding binding;

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

        edtSearch.requestFocus();
        showKeyboard();

        handleShadowScroll();
    }

    private void handleShadowScroll() {
        nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            final float dpShadow = DisplayUtils.getDisplay().dpToPx(nested.getContext(), 12);

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
            SearchModeBottomSheet searchModeBottomSheet = SearchModeBottomSheet.newInstance(search);
            searchModeBottomSheet.show(getChildFragmentManager(), null);
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
                search.setSearchMode(Search.SearchMode.TODO);
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
                            getSearchViewModel().fetch();
                            deleteDialog.dismiss();
                        });
                    });

                    moreDialog.setOnClickEdit(() -> {
                        moreDialog.dismiss();

                        Fragment fragment = AddEditTodoFragment.newInstance(todoMenu);
                        fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.add(R.id.mainContainer, fragment, Tags.FragmentTag.ADD_EDIT_TODO);
                        transaction.addToBackStack(Tags.BackStack.ADD_EDIT_TODO);
                        transaction.commit();
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
                        else
                            txtNotes.setText(Html.fromHtml(getString(R.string.todo_not_found, getTitleTerm(), getSearchViewModel().getCurrentTerm())));


                        vectorImage.setVisibility(View.VISIBLE);
                    } else {
                        txtNotes.setVisibility(View.GONE);
                        vectorImage.setVisibility(View.GONE);
                        txtResult.setText(getString(R.string.search_result, todos.size(), getTodoViewModel().getTodosCount()));
                        txtResult.setVisibility(afterTextChanged ? View.VISIBLE : View.GONE);
                        nested.setVisibility(View.VISIBLE);
                        rvSearch.setVisibility(View.VISIBLE);
                        rvSearch.post(() -> adapter.getDiffer().submitList(todos));
                    }
                }
        );
    }

    private String getTitleTerm() {
        String title;
        switch (getSearchViewModel().getSearchMode()) {
            case TODO:
            default:
                title = "عنوان";
                break;

            case CATEGORY:
                title = "دسته‌بندی";
                break;

            case BOTH:
                title = "عنوان و دسته‌بندی";
                break;
        }

        return title;
    }

}
