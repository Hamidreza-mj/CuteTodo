package viewmodel;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.Objects;

import hlv.cute.todo.R;
import model.Category;
import model.DateTime;
import model.Todo;
import utils.ResourceUtils;

public class AddEditCategoryViewModel extends ViewModel {

    private Category category;
    private boolean isEditMode = false;

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEditMode() {
        return isEditMode;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    public Category addCategory(String title) {
        category = new Category();
        category.setId(category.getId());
        category.setName(title);
        return category;
    }

    public Category editCategory(String title) {
        Category mustBeEditedCategory = new Category();
        mustBeEditedCategory.setId(category.getId());
        mustBeEditedCategory.setName(title);
        return mustBeEditedCategory;
    }

    //region: get text and strings
    public String getTitleFragment() {
        return getString(isEditMode ? R.string.edit_category : R.string.add_new_category);
    }

    public String getButtonPrimaryText() {
        return getString(isEditMode ? R.string.edit : R.string.save);
    }

    public String getEditTextTitle() {
        return isEditMode ? category.getName() : "";
    }

    //endregion

    private String getString(@StringRes int stringRes) {
        return ResourceUtils.get().getString(stringRes);
    }

}
