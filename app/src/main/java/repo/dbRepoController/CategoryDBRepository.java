package repo.dbRepoController;


import androidx.lifecycle.MutableLiveData;

import java.util.List;

import hlv.cute.todo.App;
import model.Category;
import repo.dao.CategoryDao;

public class CategoryDBRepository {

    private final CategoryDao dao;
    private final MutableLiveData<List<Category>> categories;

    private long count = 0;
    private List<Category> allCategories;

    public CategoryDBRepository() {
        dao = App.get().todoDatabase().getCategoryDao();
        categories = new MutableLiveData<>();
    }

    public void fetchAllCategories() {
        //it must call be in another thread
        //use .postValue() instead of .setValue()
        // because the .postValue() run in the background thread (non-ui thread)
        new Thread(() -> categories.postValue(dao.getAllCategories())).start();
    }

    public List<Category> getAllCategories() throws InterruptedException {
        Thread thread = new Thread(() -> allCategories = dao.getAllCategories());
        thread.start();
        thread.join();
        return allCategories;
    }

    public void addCategory(Category category) throws InterruptedException {
        Thread thread = new Thread(() -> dao.create(category));
        thread.start();
        thread.join();

        fetchAllCategories();
    }

    public void editCategory(Category category) throws InterruptedException {
        Thread thread = new Thread(() -> {
            dao.update(category);
            if (category.getId() != 0 && category.getName() != null) //maybe not needed!
                dao.editTodoCategory(category.getId(), category.getName()); //also edit all used category in todos
        });
        thread.start();
        thread.join();

        fetchAllCategories();
    }

    public void deleteCategory(Category category) throws InterruptedException {
        Thread thread = new Thread(() -> {
            dao.delete(category);
            dao.clearSingleCategory(category.getId()); //clear category from single todo
        });
        thread.start();
        thread.join();

        fetchAllCategories();
    }

    public void deleteAllCategories() throws InterruptedException {
        Thread thread = new Thread(() -> {
            dao.deleteAllCategories();
            dao.clearAllCategories(); //clear categories from all todos
        });
        thread.start();
        thread.join();

        fetchAllCategories();
    }

    public long categoriesCount() throws InterruptedException {
        Thread thread = new Thread(() -> count = dao.getCategoriesCount());
        thread.start();
        thread.join();
        return count;
    }

    public MutableLiveData<List<Category>> getCategoriesLiveData() {
        return categories;
    }
}