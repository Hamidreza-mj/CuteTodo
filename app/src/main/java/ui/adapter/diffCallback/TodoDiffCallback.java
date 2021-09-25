package ui.adapter.diffCallback;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

import model.Todo;

public class TodoDiffCallback extends DiffUtil.Callback {

    private List<Todo> oldList;
    private List<Todo> newList;

    public TodoDiffCallback(List<Todo> oldList, List<Todo> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return true;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Todo oldTodo = oldList.get(oldItemPosition);
        final Todo newTodo = newList.get(newItemPosition);

     /*   boolean areContentTheSame = oldTodo.getId() == newTodo.getId() &&
                Objects.equals(oldTodo.getTitle(), newTodo.getTitle()) &&
                Objects.equals(oldTodo.getCategory(), newTodo.getCategory()) &&
                Objects.equals(oldTodo.getPriority(), newTodo.getPriority()) &&
                oldTodo.isDone() == newTodo.isDone();*/

        int res = oldTodo.compareTo(newTodo);
        return res == 0;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }

}
