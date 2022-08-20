package repo.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.Category

@Dao
interface CategoryDao {

    @Insert(entity = Category::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(category: Category?)

    @Update(entity = Category::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(category: Category?)

    @Delete(entity = Category::class)
    suspend fun delete(category: Category?)

    @Query("SELECT * FROM categories ORDER BY id DESC;")
    fun getAllCategories(): Flow<List<Category>?>

    @Query("SELECT * FROM categories WHERE id == :categoryID;")
    suspend fun getCategory(categoryID: Long): Category

    @Query("DELETE FROM categories;")
    suspend fun deleteAllCategories()

    @Query("UPDATE todos SET category_id = 0, category = null;")
    suspend fun clearAllCategories()

    @Query("UPDATE todos SET category_id = 0, category = null WHERE category_id = :categoryID;")
    suspend fun clearSingleCategory(categoryID: Long)

    @Query("SELECT COUNT(*) FROM categories;")
    suspend fun getCategoriesCount(): Long

    @Query("UPDATE todos SET category = :categoryName WHERE category_id = :categoryID;")
    suspend fun editTodoCategory(categoryID: Long, categoryName: String?)
}