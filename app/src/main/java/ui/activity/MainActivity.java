package ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.ActivityMainBinding;
import ui.fragment.AddEditTodoFragment;
import ui.fragment.CategoriesFragment;
import ui.fragment.HomeFragment;
import utils.Constants;
import viewmodel.AddEditTodoViewModel;
import viewmodel.CategoryViewModel;
import viewmodel.SearchViewModel;
import viewmodel.TodoViewModel;

public class MainActivity extends BaseActivity {

    private TodoViewModel todoViewModel;
    private CategoryViewModel categoryViewModel;
    private SearchViewModel searchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hlv.cute.todo.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        provideViewModels();

        Intent externalIntent = getIntent();
        String action = externalIntent.getAction();
        String type = externalIntent.getType();

        if (Objects.equals(Intent.ACTION_SEND, action)) {
            if (Objects.equals(type, "text/plain")) {
                sendToNewTodo(externalIntent);
                return;
            }
        }

        initViews();
    }

    private void sendToNewTodo(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Fragment fragment = AddEditTodoFragment.newInstanceShare(sharedText);
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO);
            transaction.addToBackStack(Constants.BackStack.ADD_EDIT_TODO);
            transaction.commit();
        }
    }

    private void initViews() {
        Fragment homeFragment = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME);
        transaction.commit();
    }

    private void provideViewModels() {
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    public TodoViewModel getTodoViewModel() {
        return todoViewModel;
    }

    public CategoryViewModel getCategoryViewModel() {
        return categoryViewModel;
    }

    public SearchViewModel getSearchViewModel() {
        return searchViewModel;
    }

    @Override
    public void onBackPressed() {
        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);

        if (fragment instanceof HomeFragment) {
            HomeFragment homeFragment = ((HomeFragment) fragment);
            int scrollY = homeFragment.getScrollYPos();

            if (fragment.isVisible() && scrollY > 0 && backstackCount == 0) {
                homeFragment.goToTop(800);
                return;
            }
        } else if (fragment instanceof CategoriesFragment) {
            CategoriesFragment categoriesFragment = ((CategoriesFragment) fragment);
            int scrollY = categoriesFragment.getScrollYPos();

            if (fragment.isVisible() && scrollY > 0) {
                categoriesFragment.goToTop(800);
                return;
            }
        } else if (fragment instanceof AddEditTodoFragment) {
            AddEditTodoViewModel addEditTodoViewModel = ((AddEditTodoFragment) fragment).getViewModel();
            if (addEditTodoViewModel != null) {
                if (addEditTodoViewModel.isShareMode()) {
                    getSupportFragmentManager().popBackStack();
                    Fragment homeFragment = HomeFragment.newInstance();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME);
                    transaction.commit();
                    return;
                }
            }
        }

        super.onBackPressed();
    }
}