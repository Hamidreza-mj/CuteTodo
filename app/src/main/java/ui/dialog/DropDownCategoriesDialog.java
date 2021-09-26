package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.DialogDropdownCategoriesBinding;
import model.Category;
import ui.adapter.DropDownCategoryAdapter;

public class DropDownCategoriesDialog {

    private AlertDialog dialog;

    private final RecyclerView rvCategory;

    private final List<Category> categories;

    private OnClickCategory onClickCategory;

    public DropDownCategoriesDialog(Context context, List<Category> categories, boolean cancelable) {
        this.categories = categories;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        DialogDropdownCategoriesBinding binding = DialogDropdownCategoriesBinding.inflate(LayoutInflater.from(context), null, false);
        builder.setView(binding.getRoot());

        TextView txtClose = binding.txtClose;
        rvCategory = binding.rvCategory;

        handleRecyclerView(context);

        txtClose.setOnClickListener(view -> dismiss());

        if (dialog == null)
            dialog = builder.create();
    }

    private void handleRecyclerView(Context context) {
        DropDownCategoryAdapter adapter = new DropDownCategoryAdapter(context, category -> {
            if (onClickCategory == null) {
                dismiss();
                return;
            }

            onClickCategory.onClick(category);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvCategory.setLayoutManager(layoutManager);
        rvCategory.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rvCategory.setAdapter(adapter);

        if (categories == null || categories.isEmpty()) {

        }

        adapter.getDiffer().submitList(categories);
    }

    public DropDownCategoriesDialog(Context context, List<Category> categories) {
        this(context, categories, true);
    }

    public void setOnClickCategory(OnClickCategory onClickCategory) {
        this.onClickCategory = onClickCategory;
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void show() {
        if (dialog != null)
            dialog.show();
    }

    public interface OnClickCategory {
        void onClick(Category category);
    }

}
