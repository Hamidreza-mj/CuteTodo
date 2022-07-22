package ui.fragment.sheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.NestedScrollView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.SheetFilterBinding;
import model.Category;
import model.Filter;
import ui.adapter.FilterCategoryAdapter;
import utils.KeyboardInputHelper;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private SheetFilterBinding binding;

    private AppCompatCheckBox chkDoneTodo, chkUndoneTodo;
    private AppCompatCheckBox chkScheduled;
    private AppCompatCheckBox chkToday;
    private AppCompatCheckBox chkLowPriority, chkNormalPriority, chkHighPriority;

    private MaterialButton btnApplyFilter, btnClearFilter;

    private Filter filter;

    private static final String FILTER_ARGS = "filter-args";
    private static final String CATEGORY_ARGS = "categories-args";

    private OnApplyClick onApplyClick;
    private OnClearClick onClearClick;
    private ArrayList<Category> categories;

    private FilterCategoryAdapter adapter;

    public FilterBottomSheet() {
    }

    public static FilterBottomSheet newInstance(Filter filter, ArrayList<Category> categories) {
        FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(FILTER_ARGS, filter);
        args.putSerializable(CATEGORY_ARGS, categories);
        filterBottomSheet.setArguments(args);
        return filterBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !getArguments().isEmpty()) {
            filter = (Filter) getArguments().getSerializable(FILTER_ARGS);
            categories = (ArrayList<Category>) getArguments().getSerializable(CATEGORY_ARGS);
        }

        if (getContext() != null)
            KeyboardInputHelper.getKeyboardInput().hideKeyboard(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SheetFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initData();
        handleAction();
    }

    private void initViews() {
        chkDoneTodo = binding.aChkBoxDone;
        chkUndoneTodo = binding.aChkBoxUndone;

        chkScheduled = binding.aChkBoxScheduled;
        chkToday = binding.aChkBoxToday;

        chkLowPriority = binding.aChkBoxLow;
        chkNormalPriority = binding.aChkBoxNormal;
        chkHighPriority = binding.aChkBoxHigh;

        btnApplyFilter = binding.mBtnApplyFilter;
        btnClearFilter = binding.mBtnClearFilter;
    }

    private void initData() {
        adapter = new FilterCategoryAdapter(getContext());


        if (filter != null) {
            chkDoneTodo.setChecked(filter.isDone());
            chkUndoneTodo.setChecked(filter.isUndone());

            chkScheduled.setChecked(filter.isScheduled());
            chkToday.setChecked(filter.isToday());

            chkLowPriority.setChecked(filter.isLow());
            chkNormalPriority.setChecked(filter.isNormal());
            chkHighPriority.setChecked(filter.isHigh());

            //select item adapter
            //+ categories
            //+ must be select ids

            List<Integer> selectedIds = filter.getCategoryIds();
            for (int i = 0; i < categories.size(); i++) {
                Category tempCat = categories.get(i);
                if (selectedIds.contains(tempCat.getId()))
                    tempCat.setSelectedForFilter(true);
            }
        }
    }

    private void handleAction() {
        binding.aImgClose.setOnClickListener(view -> dismiss());

        //button
        btnApplyFilter.setOnClickListener(view -> {
            if (onApplyClick == null) {
                dismiss();
                return;
            }

            onApplyClick.onClick();
        });

        btnClearFilter.setOnClickListener(view -> {
            if (onClearClick == null) {
                dismiss();
                return;
            }

            onClearClick.onClick();
        });

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW_REVERSE);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setFlexWrap(FlexWrap.WRAP);

        binding.rvCategory.setLayoutManager(layoutManager);
        binding.rvCategory.setAdapter(adapter);
        adapter.getDiffer().submitList(categories);

        binding.nested.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            final float dpShadow = getResources().getDimension(R.dimen.toolbar_sheet_shadow);

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    binding.toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
                } else if (scrollY > 50) {
                    binding.toolbar.setTranslationZ(dpShadow);
                    binding.toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
                }
            }
        });
    }

    public void setOnApplyClick(OnApplyClick onApplyClick) {
        this.onApplyClick = onApplyClick;
    }

    public void setOnClearClick(OnClearClick onClearClick) {
        this.onClearClick = onClearClick;
    }

    public interface OnApplyClick {
        void onClick();
    }

    public interface OnClearClick {
        void onClick();
    }

    public Filter getFilter() {
        if (chkDoneTodo == null) //check one checkbox that's enough
            return null;

        Filter filter = new Filter();

        filter.setDone(chkDoneTodo.isChecked());
        filter.setUndone(chkUndoneTodo.isChecked());

        filter.setScheduled(chkScheduled.isChecked());
        filter.setToday(chkToday.isChecked());

        filter.setLow(chkLowPriority.isChecked());
        filter.setNormal(chkNormalPriority.isChecked());
        filter.setHigh(chkHighPriority.isChecked());

        filter.setCategoryIds(getSelectedIdsInAdapter());

        return filter;
    }

    private ArrayList<Integer> getSelectedIdsInAdapter() {
        ArrayList<Integer> categoryIds = new ArrayList<>();
        for (int i = 0; i < adapter.getSelectedItems().size(); i++) {
            Category tempCat = adapter.getSelectedItems().get(i);
            categoryIds.add(tempCat.getId());
        }

        return categoryIds;
    }

    public void clearFilterViews() {
        if (chkDoneTodo == null) {//check one checkbox that's enough
            dismiss();
            return;
        }

        //clear checked
        chkDoneTodo.setChecked(false);
        chkUndoneTodo.setChecked(false);

        chkScheduled.setChecked(false);
        chkToday.setChecked(false);

        chkLowPriority.setChecked(false);
        chkNormalPriority.setChecked(false);
        chkHighPriority.setChecked(false);

        clearSelectRecyclerView();

        disableViews();
    }

    private void clearSelectRecyclerView() {
        for (int i = 0; i < categories.size(); i++)
            categories.get(i).setSelectedForFilter(false);

        adapter.getDiffer().submitList(categories);
    }

    public void disableViews() {
        if (chkDoneTodo == null) { //check one checkbox that's enough
            dismiss();
            return;
        }

        chkDoneTodo.setEnabled(false);
        chkUndoneTodo.setEnabled(false);

        chkScheduled.setEnabled(false);
        chkToday.setEnabled(false);

        chkLowPriority.setEnabled(false);
        chkNormalPriority.setEnabled(false);
        chkHighPriority.setEnabled(false);
        btnApplyFilter.setEnabled(false);
        btnClearFilter.setEnabled(false);
        binding.rvCategory.setEnabled(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        try {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.setOnShowListener(dialogInterface -> {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            });

            return dialog;
        } catch (Exception e) {
            return requireDialog();
        }
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);

        ViewGroup.LayoutParams layoutParams;

        if (bottomSheet == null)
            return;

        layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null)
            layoutParams.height = windowHeight;

        bottomSheet.setLayoutParams(layoutParams);

        final BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(windowHeight, true);
        behavior.setSkipCollapsed(true); //use it for ignore setting snap to new state

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    /*if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }*/
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private int getWindowHeight() {
        try {
            if (getContext() == null)
                return 1000;

            return getContext().getResources().getDisplayMetrics().heightPixels;
        } catch (Exception e) {
            return 1000;
        }
    }

}
