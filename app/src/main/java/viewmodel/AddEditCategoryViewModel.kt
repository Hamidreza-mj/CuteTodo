package viewmodel

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import hlv.cute.todo.App
import hlv.cute.todo.R
import model.Category
import utils.AppResourcesProvider
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    private val provideResource: AppResourcesProvider
) : ViewModel() {

    var category: Category? = null
    var isEditMode = false

    fun addCategory(title: String?): Category {
        category = Category(name = title)
        return category!!
    }

    fun editCategory(title: String?): Category {
        val mustBeEditedCategory = Category().apply {
            id = category!!.id
            name = title
        }

        return mustBeEditedCategory
    }

    fun getTitleFragment(): String =
        provideResource.getString(if (isEditMode) R.string.edit_category else R.string.add_new_category)

    fun getButtonPrimaryText(): String =
        provideResource.getString(if (isEditMode) R.string.edit else R.string.save)

    fun getEditTextTitle(): String? = if (isEditMode) category!!.name else ""

}