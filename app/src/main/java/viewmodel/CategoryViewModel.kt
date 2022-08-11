package viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import model.Category
import repo.dbRepoController.CategoryDBRepository
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val dbRepository: CategoryDBRepository
) : ViewModel() {

    private val _categoriesLiveDate: MutableLiveData<List<Category>?> =
        dbRepository.categoriesLiveData

    val categoriesLiveDate: LiveData<List<Category>?> = _categoriesLiveDate

    private val _goToTopLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val goToTopLiveData: LiveData<Boolean> = _goToTopLiveData

    fun fetch() = dbRepository.fetchAllCategories()

    fun addCategory(category: Category?) {
        try {
            dbRepository.addCategory(category)
        } catch (ignored: InterruptedException) {
        }
    }

    fun getAllCategories(): ArrayList<Category>? {
        return try {
            dbRepository.getAllCategories() as ArrayList<Category>
        } catch (e: InterruptedException) {
            null
        }
    }

    fun editCategory(category: Category?) {
        try {
            dbRepository.editCategory(category!!)
        } catch (ignored: InterruptedException) {
        }
    }

    fun deleteCategory(category: Category?) {
        try {
            dbRepository.deleteCategory(category!!)
        } catch (ignored: InterruptedException) {
        }
    }

    fun deleteAllCategories() {
        try {
            dbRepository.deleteAllCategories()
        } catch (ignored: InterruptedException) {
        }
    }

    fun getCategoriesCount(): Long {
        return try {
            dbRepository.categoriesCount()
        } catch (e: InterruptedException) {
            0
        }
    }

    fun categoriesIsEmpty(): Boolean {
        return getCategoriesCount() == 0L
    }

    private fun pureValidateCategory(category: Category): Boolean {
        val categoryName = category.name
        return categoryName != null &&
                categoryName.trim().isNotEmpty() &&
                categoryName.trim().isNotEmpty()
    }

    fun validateCategory(category: Category): String? {
        val isValidCategory = pureValidateCategory(category)
        return if (!isValidCategory) "عنوان دسته نمی تواند خالی باشد!" else null
    }

    fun goToTop() {
        _goToTopLiveData.value = true
    }

}