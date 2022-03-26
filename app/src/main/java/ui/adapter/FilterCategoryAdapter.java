package ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hlv.cute.todo.databinding.ItemCategoryFilterBinding;
import model.Category;

public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder> {

    private final Context context;
    private final AsyncListDiffer<Category> differ;

    public FilterCategoryAdapter(Context context) {
        this.context = context;

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
        ItemCategoryFilterBinding binding = ItemCategoryFilterBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (differ.getCurrentList().get(position) != null)
            holder.bind(differ.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public List<Category> getSelectedItems() {
        List<Category> selected = new ArrayList<>();

        for (int i = 0; i < differ.getCurrentList().size(); i++) {
            Category temp = differ.getCurrentList().get(i);

            if (temp.isSelectedForFilter())
                selected.add(temp);
        }

        return selected;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryFilterBinding binding;

        public ViewHolder(@NonNull ItemCategoryFilterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bind(Category category) {
            if (category == null)
                return;

            binding.chip.setText(category.getName());

            binding.chip.setOnCheckedChangeListener(null);
            binding.chip.setChecked(false);
            binding.chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    category.setSelectedForFilter(isChecked);
                }
            });
        }
    }

    public AsyncListDiffer<Category> getDiffer() {
        return differ;
    }
}
