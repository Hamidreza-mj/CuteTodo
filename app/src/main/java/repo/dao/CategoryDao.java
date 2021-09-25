package repo.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import model.Category;

@Dao
public interface CategoryDao {

    @Insert(entity = Category.class, onConflict = OnConflictStrategy.REPLACE)
    void create(Category category);

    @Update(entity = Category.class, onConflict = OnConflictStrategy.REPLACE)
    void update(Category category);

    @Delete(entity = Category.class)
    void delete(Category category);


    @Query("SELECT * FROM categories ORDER BY id DESC;")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE id == :categoryID;")
    Category getCategory(long categoryID);

    @Query("DELETE FROM categories;")
    void deleteAllCategories();

    @Query("SELECT COUNT(*) FROM categories;")
    long getCategoriesCount();

}
