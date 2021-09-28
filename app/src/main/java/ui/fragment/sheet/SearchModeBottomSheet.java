package ui.fragment.sheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.SheetSearchModeBinding;
import model.Search;
import utils.KeyboardInputHelper;

public class SearchModeBottomSheet extends BottomSheetDialogFragment {

    private SheetSearchModeBinding binding;

    private RadioGroup radioGP;
    private RadioButton rdbTodo, rdbCategory, rdbBoth;

    private Search search;

    private static final String SEARCH_MODE_ARGS = "search-mode-args";

    public SearchModeBottomSheet() {
    }

    public static SearchModeBottomSheet newInstance(Search search) {
        SearchModeBottomSheet bottomSheet = new SearchModeBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(SEARCH_MODE_ARGS, search);
        bottomSheet.setArguments(args);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !getArguments().isEmpty())
            search = (Search) getArguments().getSerializable(SEARCH_MODE_ARGS);

        if (getContext() != null)
            KeyboardInputHelper.getKeyboardInput().hideKeyboard(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SheetSearchModeBinding.inflate(inflater, container, false);
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
        radioGP = binding.radioGP;
        rdbTodo = binding.radioBtnTodo;
        rdbCategory = binding.radioBtnCategory;
        rdbBoth = binding.radioBtnBoth;
    }

    @SuppressLint("NonConstantResourceId")
    private void initData() {
        if (search != null) {
            switch (radioGP.getCheckedRadioButtonId()) {
                case R.id.radioBtnTodo:
                default:
                    rdbTodo.setChecked(true);
                    break;

                case R.id.radioBtnCategory:
                    rdbCategory.setChecked(true);
                    break;

                case R.id.radioBtnBoth:
                    rdbBoth.setChecked(true);
                    break;
            }
        }
    }

    private void handleAction() {
        binding.cardClose.setOnClickListener(view -> dismiss());
        binding.mBtnClose.setOnClickListener(view -> dismiss());


    }

    @SuppressLint("NonConstantResourceId")
    public Search getSearch() {
        if (radioGP == null || search == null) //check one checkbox that's enough
            return null;

        switch (radioGP.getCheckedRadioButtonId()) {
            case R.id.radioBtnTodo:
            default:
                search.setSearchMode(Search.SearchMode.TODO);
                break;

            case R.id.radioBtnCategory:
                search.setSearchMode(Search.SearchMode.CATEGORY);
                break;

            case R.id.radioBtnBoth:
                search.setSearchMode(Search.SearchMode.BOTH);
                break;
        }

        return search;
    }

    public void disableViews() {
        if (radioGP == null) { //check one checkbox that's enough
            dismiss();
            return;
        }

        radioGP.setEnabled(false);
        rdbTodo.setEnabled(false);
        rdbCategory.setEnabled(false);
        rdbBoth.setEnabled(false);
    }

}
