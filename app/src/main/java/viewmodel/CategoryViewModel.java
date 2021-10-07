package viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Category;
import repo.dbRepoController.CategoryDBRepository;

public class CategoryViewModel extends ViewModel {

    private final CategoryDBRepository dbRepository;
    private final MutableLiveData<List<Category>> categoriesLiveDate;

    private final MutableLiveData<Boolean> goToTopLiveData;

    public CategoryViewModel() {
        dbRepository = new CategoryDBRepository();
        categoriesLiveDate = dbRepository.getCategoriesLiveData();
        goToTopLiveData = new MutableLiveData<>();
    }

    public void fetch() {
        dbRepository.fetchAllCategories();
    }

    public List<Category> getAllCategories() {
        try {
            return dbRepository.getAllCategories();
        } catch (InterruptedException e) {
            return null;
        }

    }

    public void addCategory(Category category) {
        try {
            dbRepository.addCategory(category);
        } catch (InterruptedException ignored) {
        }
    }

    public void editCategory(Category category) {
        try {
            dbRepository.editCategory(category);
        } catch (InterruptedException ignored) {
        }
    }

    public void deleteCategory(Category category) {
        try {
            dbRepository.deleteCategory(category);
        } catch (InterruptedException ignored) {
        }
    }

    public void deleteAllCategories() {
        try {
            dbRepository.deleteAllCategories();
        } catch (InterruptedException ignored) {
        }
    }

    public long getCategoriesCount() {
        try {
            return dbRepository.categoriesCount();
        } catch (InterruptedException e) {
            return 0;
        }
    }

    public boolean categoriesIsEmpty() {
        return getCategoriesCount() == 0;
    }

    public boolean pureValidateCategory(Category category) {
        String categoryName = category.getName();
        return categoryName != null && !categoryName.trim().isEmpty() && categoryName.trim().length() != 0;
    }

    public String validateCategory(Category category) {
        boolean isValidCategory = pureValidateCategory(category);

        if (!isValidCategory)
            return "عنوان دسته نمی تواند خالی باشد!";

        return null;
    }

    public LiveData<List<Category>> getCategoriesLiveDate() {
        return categoriesLiveDate;
    }

    public void goToTop() {
        if (goToTopLiveData != null)
            goToTopLiveData.setValue(true);
    }

    public LiveData<Boolean> getGoToTopLiveData() {
        return goToTopLiveData;
    }
}
