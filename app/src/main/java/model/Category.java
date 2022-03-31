package model;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Keep
@Entity(tableName = "categories")
public class Category implements Comparable<Category>, Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @Ignore
    private boolean selectedForFilter = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelectedForFilter() {
        return selectedForFilter;
    }

    public void setSelectedForFilter(boolean selectedForFilter) {
        this.selectedForFilter = selectedForFilter;
    }

    @Override
    public int compareTo(Category category) {
        boolean isSame = id == category.getId() &&
                Objects.equals(name, category.getName()) &&
                selectedForFilter == category.isSelectedForFilter();

        if (isSame)
            return 0;

        return 1;
    }
}
