package ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import hlv.cute.todo.databinding.ItemTodoBinding;
import model.Todo;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private final Context context;
    private final AsyncListDiffer<Todo> differ;

    public TodoAdapter(Context context) {
        this.context = context;

        DiffUtil.ItemCallback<Todo> diffCallback = new DiffUtil.ItemCallback<Todo>() {
            @Override
            public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
                return Objects.equals(oldItem, newItem);
            }
        };

        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTodoBinding binding = ItemTodoBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(differ.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatCheckBox checkBox;
        private final AppCompatImageView imgMenu;
        private final TextView txtCategory;

        private final TextView txtLow, txtNormal, txtHigh;

        public ViewHolder(@NonNull ItemTodoBinding binding) {
            super(binding.getRoot());

            checkBox = binding.aChkBoxTitle;
            imgMenu = binding.aImgMenu;
            txtCategory = binding.txtCategory;

            txtLow = binding.txtLowPriority;
            txtNormal = binding.txtNormalPriority;
            txtHigh = binding.txtHighPriority;
        }

        private void bind(Todo todo) {
            checkBox.setChecked(todo.isDone());
            checkBox.setText(todo.getTitle());
            imgMenu.setOnClickListener(view -> {
            });
            txtCategory.setText(todo.getCategory());

            switch (todo.getPriority()) {
                case LOW:
                default:
                    txtLow.setVisibility(View.VISIBLE);
                    txtNormal.setVisibility(View.GONE);
                    txtHigh.setVisibility(View.GONE);
                    break;
                case NORMAL:
                    txtNormal.setVisibility(View.VISIBLE);
                    txtLow.setVisibility(View.GONE);
                    txtHigh.setVisibility(View.GONE);
                    break;
                case HIGH:
                    txtHigh.setVisibility(View.VISIBLE);
                    txtLow.setVisibility(View.GONE);
                    txtNormal.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public AsyncListDiffer<Todo> getDiffer() {
        return differ;
    }
}