package model;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Keep
public class Filter implements Serializable {

    private boolean isDone;
    private boolean isUndone;

    private boolean isScheduled;
    private boolean isToday;

    private boolean isLow;
    private boolean isNormal;
    private boolean isHigh;

    private final List<Priority> priorities;

    public Filter() {
        priorities = new ArrayList<>();
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
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
        if (low) priorities.add(Priority.LOW);
        isLow = low;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        if (normal) priorities.add(Priority.NORMAL);
        isNormal = normal;
    }

    public boolean isHigh() {
        return isHigh;
    }

    public void setHigh(boolean high) {
        if (high) priorities.add(Priority.HIGH);
        isHigh = high;
    }

    public List<Priority> getPriorities() {
        return priorities;
    }

    /**
     * add all priority when no priority selected
     *
     * @return list of priorities
     */
    public List<Priority> addAllPriorities() {
        priorities.add(Priority.LOW);
        priorities.add(Priority.NORMAL);
        priorities.add(Priority.HIGH);

        return priorities;
    }

    public boolean filterIsEmpty() {
        boolean emptyFilter = !isDone && !isUndone &&
                !isScheduled && !isToday &&
                !isLow && !isNormal && !isHigh && priorities.isEmpty() ||
                !isDone && !isUndone && !isScheduled && !isToday && !isLow && !isNormal && !isHigh && priorities.size() == 3;

        return emptyFilter;
    }
}
