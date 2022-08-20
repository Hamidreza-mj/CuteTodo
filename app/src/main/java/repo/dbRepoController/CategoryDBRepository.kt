package repo.dbRepoController

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import model.Category
import repo.dao.CategoryDao
import javax.inject.Inject

@ViewModelScoped
class CategoryDBRepository @Inject constructor(
    private val dao: CategoryDao
) {

    fun getAllCategories(): Flow<List<Category>?> = dao.getAllCategories().distinctUntilChanged()

    suspend fun categoriesCount(): Long {
        return dao.getCategoriesCount()
    }

    suspend fun addCategory(category: Category?) {
        dao.create(category)
    }

    suspend fun editCategory(category: Category) {
        dao.update(category)

        if (category.id != 0 && category.name != null) //maybe not needed!
            dao.editTodoCategory(
                category.id.toLong(),
                category.name
            ) //also edit all used category in todos
    }

    suspend fun deleteCategory(category: Category) {
        dao.delete(category)
        dao.clearSingleCategory(category.id.toLong()) //clear category from single todo
    }

    suspend fun deleteAllCategories() {
        dao.deleteAllCategories()

        dao.clearAllCategories() //clear categories from all todos
    }

}