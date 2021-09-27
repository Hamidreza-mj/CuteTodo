package ui.fragment.sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import hlv.cute.todo.databinding.SheetFilterBinding;
import model.Filter;
import utils.KeyboardInputHelper;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private SheetFilterBinding binding;

    private MaterialCardView cardClose;

    private AppCompatCheckBox chkDoneTodo, chkUndoneTodo;
    private AppCompatCheckBox chkLowPriority, chkNormalPriority, chkHighPriority;

    private MaterialButton btnApplyFilter, btnClearFilter;

    private Filter filter;

    private static final String FILTER_ARGS = "filter-args";

    private OnApplyClick onApplyClick;
    private OnClearClick onClearClick;

    public FilterBottomSheet() {
    }

    public static FilterBottomSheet newInstance(Filter filter) {
        FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(FILTER_ARGS, filter);
        filterBottomSheet.setArguments(args);
        return filterBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !getArguments().isEmpty())
            filter = (Filter) getArguments().getSerializable(FILTER_ARGS);

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
        cardClose = binding.cardClose;

        chkDoneTodo = binding.aChkBoxDone;
        chkUndoneTodo = binding.aChkBoxUndone;

        chkLowPriority = binding.aChkBoxLow;
        chkNormalPriority = binding.aChkBoxNormal;
        chkHighPriority = binding.aChkBoxHigh;

        btnApplyFilter = binding.mBtnApplyFilter;
        btnClearFilter = binding.mBtnClearFilter;
    }

    private void initData() {
        if (filter != null) {
            chkDoneTodo.setChecked(filter.isDone());
            chkUndoneTodo.setChecked(filter.isUndone());

            chkLowPriority.setChecked(filter.isLow());
            chkNormalPriority.setChecked(filter.isNormal());
            chkHighPriority.setChecked(filter.isHigh());
        }
    }

    private void handleAction() {
        cardClose.setOnClickListener(view -> dismiss());

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

        filter.setLow(chkLowPriority.isChecked());
        filter.setNormal(chkNormalPriority.isChecked());
        filter.setHigh(chkHighPriority.isChecked());

        return filter;
    }

    public void clearCheckBoxes() {
        if (chkDoneTodo == null) {//check one checkbox that's enough
            dismiss();
            return;
        }

        //clear checked
        chkDoneTodo.setChecked(false);
        chkUndoneTodo.setChecked(false);

        chkLowPriority.setChecked(false);
        chkNormalPriority.setChecked(false);
        chkHighPriority.setChecked(false);

        disableViews();
    }

    public void disableViews() {
        if (chkDoneTodo == null) { //check one checkbox that's enough
            dismiss();
            return;
        }

        chkDoneTodo.setEnabled(false);
        chkUndoneTodo.setEnabled(false);

        chkLowPriority.setEnabled(false);
        chkNormalPriority.setEnabled(false);
        chkHighPriority.setEnabled(false);
        btnApplyFilter.setEnabled(false);
        btnClearFilter.setEnabled(false);
    }

}
