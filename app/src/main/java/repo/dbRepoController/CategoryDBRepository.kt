package repo.dbRepoController

import androidx.lifecycle.MutableLiveData
import model.Category
import repo.dao.CategoryDao
import javax.inject.Inject

class CategoryDBRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    val categoriesLiveData: MutableLiveData<List<Category>?> = MutableLiveData()

    private var count: Long = 0

    fun fetchAllCategories() {
        //it must call be in another thread
        //use .postValue() instead of .setValue()
        // because the .postValue() run in the background thread (non-ui thread)
        Thread {
            categoriesLiveData.postValue(categoryDao.getAllCategories())
        }.start()
    }

    @Throws(InterruptedException::class)
    fun getAllCategories(): List<Category>? {
        var allCategories: List<Category>? = null

        Thread {
            allCategories = categoryDao.getAllCategories()
        }.apply {
            start()
            join()
        }

        return allCategories
    }

    @Throws(InterruptedException::class)
    fun addCategory(category: Category?) {
        Thread {
            categoryDao.create(category)
        }.apply {
            start()
            join()
        }

        fetchAllCategories()
    }

    @Throws(InterruptedException::class)
    fun editCategory(category: Category) {
        Thread {
            categoryDao.update(category)

            if (category.id != 0 && category.name != null) //maybe not needed!
                categoryDao.editTodoCategory(
                    category.id.toLong(),
                    category.name
                ) //also edit all used category in todos
        }.apply {
            start()
            join()
        }

        fetchAllCategories()
    }

    @Throws(InterruptedException::class)
    fun deleteCategory(category: Category) {
        Thread {
            categoryDao.delete(category)
            categoryDao.clearSingleCategory(category.id.toLong()) //clear category from single todo
        }.apply {
            start()
            join()
        }

        fetchAllCategories()
    }

    @Throws(InterruptedException::class)
    fun deleteAllCategories() {
        Thread {
            categoryDao.deleteAllCategories()
            categoryDao.clearAllCategories() //clear categories from all todos
        }.apply {
            start()
            join()
        }

        fetchAllCategories()
    }

    @Throws(InterruptedException::class)
    fun categoriesCount(): Long {
        Thread {
            count = categoryDao.getCategoriesCount()
        }.apply {
            start()
            join()
        }

        return count
    }

}