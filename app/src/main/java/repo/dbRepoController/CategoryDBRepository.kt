package repo.dbRepoController

import androidx.lifecycle.MutableLiveData
import hlv.cute.todo.App
import model.Category
import repo.dao.CategoryDao

class CategoryDBRepository {

    private val dao: CategoryDao? = App.get()!!.todoDatabase()!!.categoryDao

    val categoriesLiveData: MutableLiveData<List<Category>?> = MutableLiveData()

    private var count: Long = 0

    fun fetchAllCategories() {
        //it must call be in another thread
        //use .postValue() instead of .setValue()
        // because the .postValue() run in the background thread (non-ui thread)
        Thread {
            categoriesLiveData.postValue(dao!!.getAllCategories())
        }.start()
    }

    @Throws(InterruptedException::class)
    fun getAllCategories(): List<Category>? {
        var allCategories: List<Category>? = null

        Thread {
            allCategories = dao!!.getAllCategories()
        }.apply {
            start()
            join()
        }

        return allCategories
    }

    @Throws(InterruptedException::class)
    fun addCategory(category: Category?) {
        Thread {
            dao!!.create(category)
        }.apply {
            start()
            join()
        }

        fetchAllCategories()
    }

    @Throws(InterruptedException::class)
    fun editCategory(category: Category) {
        Thread {
            dao!!.update(category)

            if (category.id != 0 && category.name != null) //maybe not needed!
                dao.editTodoCategory(
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
            dao!!.delete(category)
            dao.clearSingleCategory(category.id.toLong()) //clear category from single todo
        }.apply {
            start()
            join()
        }

        fetchAllCategories()
    }

    @Throws(InterruptedException::class)
    fun deleteAllCategories() {
        Thread {
            dao!!.deleteAllCategories()
            dao.clearAllCategories() //clear categories from all todos
        }.apply {
            start()
            join()
        }

        fetchAllCategories()
    }

    @Throws(InterruptedException::class)
    fun categoriesCount(): Long {
        Thread {
            count = dao!!.getCategoriesCount()
        }.apply {
            start()
            join()
        }

        return count
    }

}