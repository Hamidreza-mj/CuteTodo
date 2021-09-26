package ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import hlv.cute.todo.databinding.ItemDropdownBinding;
import model.Category;

public class DropDownCategoryAdapter extends RecyclerView.Adapter<DropDownCategoryAdapter.ViewHolder> {

    private final Context context;
    private final AsyncListDiffer<Category> differ;

    private final OnClickCategoryListener onClickCategoryListener;

    public DropDownCategoryAdapter(Context context, OnClickCategoryListener onClickCategoryListener) {
        this.context = context;
        this.onClickCategoryListener = onClickCategoryListener;

        DiffUtil.ItemCallback<Category> diffCallback = new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.compareTo(newItem) == 0;
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDropdownBinding binding = ItemDropdownBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(differ.getCurrentList().get(position), onClickCategoryListener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtCategory;

        public ViewHolder(@NonNull ItemDropdownBinding binding) {
            super(binding.getRoot());

            txtCategory = binding.txt;
        }

        private void bind(Category category, OnClickCategoryListener onClickCategoryListener) {
            txtCategory.setText(category.getName());

            txtCategory.setOnClickListener(view -> onClickCategoryListener.onClick(category));
        }
    }

    public AsyncListDiffer<Category> getDiffer() {
        return differ;
    }

    public interface OnClickCategoryListener {
        void onClick(Category category);
    }

}
