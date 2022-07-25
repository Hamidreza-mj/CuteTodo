package viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import hlv.cute.todo.R
import model.Category
import utils.ResourceUtils

class AddEditCategoryViewModel : ViewModel() {

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
        getString(if (isEditMode) R.string.edit_category else R.string.add_new_category)

    fun getButtonPrimaryText(): String =
        getString(if (isEditMode) R.string.edit else R.string.save)

    fun getEditTextTitle(): String? = if (isEditMode) category!!.name else ""

    private fun getString(@StringRes stringRes: Int): String {
        return ResourceUtils.get().getString(stringRes)
    }
}