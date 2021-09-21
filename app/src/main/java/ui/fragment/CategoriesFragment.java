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

import java.util.ArrayList;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.FragmentCategoriesBinding;
import hlv.cute.todo.databinding.FragmentHomeBinding;
import model.Category;
import model.Todo;
import ui.adapter.CategoryAdapter;
import ui.adapter.TodoAdapter;
import ui.dialog.GlobalMenuDialog;
import ui.dialog.MoreDialog;
import utils.DisplayUtils;
import utils.Tags;

public class CategoriesFragment extends BaseFragment {

    private FragmentCategoriesBinding binding;

    private ConstraintLayout toolbar;
    private NestedScrollView nested;
    private RecyclerView rvCategory;
    private FrameLayout frameLytButton;
    private MaterialButton btnAdd;
    private HideBottomViewOnScrollBehavior<FrameLayout> scrollBehavior;

    private CategoryAdapter adapter;
    private int scrollYPos = 0;

    public CategoriesFragment() {
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        handleActions();
    }

    private void initViews() {
        binding.aImgBack.setOnClickListener(view -> back(Tags.BackStack.CATEGORY));

        toolbar = binding.toolbar;
        nested = binding.nested;
        rvCategory = binding.rvCategory;
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
        btnAdd.setOnClickListener(view -> {
            Fragment fragment = AddEditCategoryFragment.newInstance();
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, fragment, Tags.FragmentTag.ADD_EDIT_CATEGORY);
            transaction.addToBackStack(Tags.BackStack.ADD_EDIT_CATEGORY);
            transaction.commit();
        });

        handleRecyclerView();
    }

    private void handleRecyclerView() {
        if (getActivity() == null)
            return;

        adapter = new CategoryAdapter(getActivity(),
                category -> {
                    MoreDialog moreDialog = new MoreDialog(getActivity());
                    moreDialog.show();
                }
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvCategory.setLayoutManager(layoutManager);
        rvCategory.setAdapter(adapter);

        ArrayList<Category> list = new ArrayList<>();

        Category category1 = new Category();
        category1.setId(1);
        category1.setCategory("دانشگاه");

        Category category2 = new Category();
        category2.setId(2);
        category2.setCategory("کاری");

        Category category3 = new Category();
        category3.setId(3);
        category3.setCategory("منزل");

        list.add(category1);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);
        list.add(category3);


        adapter.getDiffer().submitList(list);
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
