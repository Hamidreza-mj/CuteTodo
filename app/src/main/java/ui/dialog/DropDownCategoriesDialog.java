package ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
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
    private final ConstraintLayout clytEmpty;

    private final List<Category> categories;

    private OnClickCategory onClickCategory;
    private OnclickManage onclickManage;

    public DropDownCategoriesDialog(Context context, List<Category> categories, boolean cancelable) {
        this.categories = categories;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.TranslucentDialog);
        builder.setCancelable(cancelable);
        DialogDropdownCategoriesBinding binding = DialogDropdownCategoriesBinding.inflate(LayoutInflater.from(context), null, false);
        builder.setView(binding.getRoot());

        clytEmpty = binding.cLytEmpty;
        rvCategory = binding.rvCategory;

        TextView txtClose = binding.txtClose;
        TextView txtManageCategories = binding.txtManageCategories;

        handleRecyclerView(context);

        txtClose.setOnClickListener(view -> dismiss());

        txtManageCategories.setOnClickListener(view -> {
            if (onclickManage == null) {
                dismiss();
                return;
            }

            onclickManage.onClick();
        });

        if (dialog == null)
            dialog = builder.create();
    }

    private void handleRecyclerView(Context context) {
        if (categories == null || categories.isEmpty()) {
            rvCategory.setVisibility(View.GONE);
            clytEmpty.setVisibility(View.VISIBLE);
            return;
        }

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

        clytEmpty.setVisibility(View.GONE);
        rvCategory.setVisibility(View.VISIBLE);

        //add first item for without category
        Category firstCat = new Category();
        firstCat.setId(0);
        firstCat.setName(null);
        categories.add(0, firstCat);

        adapter.getDiffer().submitList(categories);
    }

    public DropDownCategoriesDialog(Context context, List<Category> categories) {
        this(context, categories, true);
    }

    public void setOnClickCategory(OnClickCategory onClickCategory) {
        this.onClickCategory = onClickCategory;
    }

    public void setOnclickManage(OnclickManage onclickManage) {
        this.onclickManage = onclickManage;
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

    public interface OnclickManage {
        void onClick();
    }

}
