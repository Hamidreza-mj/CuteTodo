package repo.dao

import androidx.room.*
import model.Category

@Dao
interface CategoryDao {

    @Insert(entity = Category::class, onConflict = OnConflictStrategy.REPLACE)
    fun create(category: Category?)

    @Update(entity = Category::class, onConflict = OnConflictStrategy.REPLACE)
    fun update(category: Category?)

    @Delete(entity = Category::class)
    fun delete(category: Category?)

    @Query("SELECT * FROM categories ORDER BY id DESC;")
    fun getAllCategories(): List<Category>?

    @Query("SELECT * FROM categories WHERE id == :categoryID;")
    fun getCategory(categoryID: Long): Category?

    @Query("DELETE FROM categories;")
    fun deleteAllCategories()

    @Query("UPDATE todos SET category_id = 0, category = null;")
    fun clearAllCategories()

    @Query("UPDATE todos SET category_id = 0, category = null WHERE category_id = :categoryID;")
    fun clearSingleCategory(categoryID: Long)

    @Query("SELECT COUNT(*) FROM categories;")
    fun getCategoriesCount(): Long

    @Query("UPDATE todos SET category = :categoryName WHERE category_id = :categoryID;")
    fun editTodoCategory(categoryID: Long, categoryName: String?)
}