package ui.fragment.sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;

import hlv.cute.todo.databinding.SheetFilterBinding;
import utils.KeyboardInputHelper;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private SheetFilterBinding binding;

    private MaterialCardView cardClose;

    public FilterBottomSheet() {
    }

    public static FilterBottomSheet newInstance() {
        FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("", "");
        filterBottomSheet.setArguments(args);
        return filterBottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments() != null) {
            zekr = (Zekr) getArguments().getSerializable(AppConstants.NEW_ZEKR_FRAGMENT_KEY);
        }*/

        if (getContext() == null)
            return;
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
        handleAction();
    }

    private void initViews() {
        cardClose = binding.cardClose;
    }

    private void handleAction() {
        cardClose.setOnClickListener(view -> dismiss());
    }
}
