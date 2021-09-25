package model;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Keep
@Entity(tableName = "todos")
public class Todo implements Comparable<Todo> {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "priority")
    private Priority priority;

    @ColumnInfo(name = "is_done")
    private boolean isDone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public int compareTo(Todo todo) {
        if (id == todo.getId() &&
                Objects.equals(title, todo.getTitle()) &&
                Objects.equals(category, todo.category) &&
                Objects.equals(priority, todo.priority) &&
                isDone == todo.isDone())
            return 0;

        return 1;
    }

    public enum Priority {
        LOW, NORMAL, HIGH
    }

/*
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Todo)) return false;
        Todo todo = (Todo) o;
        return this.compareTo(todo) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
*/

}
