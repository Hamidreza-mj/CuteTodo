package ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import hlv.cute.todo.databinding.ItemCategoryBinding;
import model.Category;
import ui.adapter.event.OnClickMenuListener;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final AsyncListDiffer<Category> differ;

    private OnClickMenuListener onClickMenuListener;

    public CategoryAdapter(Context context, OnClickMenuListener onClickMenuListener) {
        this.context = context;
        this.onClickMenuListener = onClickMenuListener;

        DiffUtil.ItemCallback<Category> diffCallback = new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return Objects.equals(oldItem, newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(differ.getCurrentList().get(position), onClickMenuListener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatImageView imgMenu;
        private final TextView txtCategory;

        public ViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());

            imgMenu = binding.aImgMenu;
            txtCategory = binding.txtTitle;
        }

        private void bind(Category category, OnClickMenuListener onClickMenuListener) {
            imgMenu.setOnClickListener(view -> onClickMenuListener.onClick(category));
            txtCategory.setText(category.getCategory());
        }
    }

    public AsyncListDiffer<Category> getDiffer() {
        return differ;
    }
}
