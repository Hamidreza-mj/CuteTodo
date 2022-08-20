package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import model.Category
import repo.dbRepoController.CategoryDBRepository
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repo: CategoryDBRepository
) : ViewModel() {

    val categoriesFlow: SharedFlow<List<Category>?> = repo.getAllCategories().shareIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        1
    )

    //--------------channels----------------
    private val _goToTopChannel: Channel<Boolean> = Channel()
    val goToTopFlow: Flow<Boolean> = _goToTopChannel.receiveAsFlow()

    suspend fun getCategoriesCount(): Long {
        var count = 0L

        viewModelScope.launch(Dispatchers.IO) {
            count = repo.categoriesCount()
        }.join()

        return count
    }

    suspend fun categoriesIsEmpty(): Boolean {
        var isEmpty = false

        viewModelScope.launch(Dispatchers.IO) {
            isEmpty = repo.categoriesCount() == 0L
        }.join()

        return isEmpty
    }

    fun addCategory(category: Category?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addCategory(category)
        }
    }

    fun editCategory(category: Category?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.editCategory(category!!)
        }
    }

    fun deleteCategory(category: Category?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteCategory(category!!)
        }
    }

    fun deleteAllCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAllCategories()
        }
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
        viewModelScope.launch {
            _goToTopChannel.send(true)
        }
    }

}