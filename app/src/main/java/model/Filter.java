package model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Keep
public class Filter implements Serializable {

    private boolean isDone;
    private boolean isUndone;
    private boolean isLow;
    private boolean isNormal;
    private boolean isHigh;

    private final List<Todo.Priority> priorities;

    public Filter() {
        priorities = new ArrayList<>();
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isUndone() {
        return isUndone;
    }

    public void setUndone(boolean undone) {
        isUndone = undone;
    }

    public boolean isLow() {
        return isLow;
    }

    public void setLow(boolean low) {
        if (low) priorities.add(Todo.Priority.LOW);
        isLow = low;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        if (normal) priorities.add(Todo.Priority.NORMAL);
        isNormal = normal;
    }

    public boolean isHigh() {
        return isHigh;
    }

    public void setHigh(boolean high) {
        if (high) priorities.add(Todo.Priority.HIGH);
        isHigh = high;
    }

    public List<Todo.Priority> getPriorities() {
        return priorities;
    }

    /**
     * add all priority when no priority selected
     *
     * @return list of priorities
     */
    public List<Todo.Priority> addAllPriorities() {
        priorities.add(Todo.Priority.LOW);
        priorities.add(Todo.Priority.NORMAL);
        priorities.add(Todo.Priority.HIGH);

        return priorities;
    }
}
