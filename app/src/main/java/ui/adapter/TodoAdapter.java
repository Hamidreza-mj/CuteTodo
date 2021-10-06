package ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import hlv.cute.todo.databinding.ItemTodoBinding;
import model.Todo;
import utils.TextHelper;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private final Context context;
    private final AsyncListDiffer<Todo> differ;

    private final OnCheckChangedListener onCheckChangedListener;
    private final OnClickMenuListener onClickMenuListener;

    public TodoAdapter(Context context, OnCheckChangedListener onCheckChangedListener, OnClickMenuListener onClickMenuListener) {
        this.context = context;
        this.onCheckChangedListener = onCheckChangedListener;
        this.onClickMenuListener = onClickMenuListener;

        DiffUtil.ItemCallback<Todo> diffCallback = new DiffUtil.ItemCallback<Todo>() {
            @Override
            public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
                return oldItem.compareTo(newItem) == 0;
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
        holder.bind(differ.getCurrentList().get(position), onCheckChangedListener, onClickMenuListener);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatCheckBox checkBox;
        private final AppCompatImageView imgMenu;
        private final TextView txtCategory;

        private final ConstraintLayout lytDate;
        private final TextView txtDate, txtClock;

        private final TextView txtLow, txtNormal, txtHigh;

        public ViewHolder(@NonNull ItemTodoBinding binding) {
            super(binding.getRoot());

            checkBox = binding.aChkBoxTitle;
            imgMenu = binding.aImgMenu;
            txtCategory = binding.txtCategory;

            lytDate = binding.lytDate;
            txtDate = binding.txtDate;
            txtClock = binding.txtClock;

            txtLow = binding.txtLowPriority;
            txtNormal = binding.txtNormalPriority;
            txtHigh = binding.txtHighPriority;
        }

        private void bind(Todo todo, OnCheckChangedListener onCheckChangedListener, OnClickMenuListener onClickMenuListener) {
            //must be set null
            //to avoid when recyclerview scroll, default implemented interface body called!
            checkBox.setOnCheckedChangeListener(null);

            //for first run
            checkBox.setChecked(todo.isDone());
            checkBox.setText(todo.getTitle());

            if (todo.isDone())
                TextHelper.addLineThrough(checkBox);
            else
                TextHelper.removeLineThrough(checkBox);

            checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                //after checked
                if (compoundButton.isPressed()) {
                    todo.setDone(checked);

                    if (todo.isDone())
                        TextHelper.addLineThrough(checkBox);
                    else
                        TextHelper.removeLineThrough(checkBox);

                    onCheckChangedListener.onChange(todo.getId());
                }
            });


            imgMenu.setOnClickListener(view -> onClickMenuListener.onClick(todo));

            if (todo.getCategory() != null) {
                txtCategory.setVisibility(View.VISIBLE);
                txtCategory.setText(todo.getCategory());
            } else {
                txtCategory.setVisibility(View.GONE);
            }

            if (todo.getArriveDate() != 0) {
                lytDate.setVisibility(View.VISIBLE);
                txtDate.setText(todo.getDateTime().getPersianDate());
                txtClock.setText(todo.getClock());
            } else {
                lytDate.setVisibility(View.GONE);
            }

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

    public interface OnCheckChangedListener {
        void onChange(int todoID);
    }

    public interface OnClickMenuListener {
        void onClick(Todo todo);
    }
}
