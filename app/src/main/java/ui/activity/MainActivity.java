package ui.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.ActivityMainBinding;
import ui.fragment.CategoriesFragment;
import ui.fragment.HomeFragment;
import utils.Tags;
import viewmodel.TodoViewModel;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private TodoViewModel todoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
        handleTodoObserver();
    }

    private void initViews() {
        Fragment homeFragment = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.mainContainer, homeFragment, Tags.FragmentTag.HOME);
        transaction.commit();
    }

    private void handleTodoObserver() {
        todoViewModel = new ViewModelProvider(this).get(TodoViewModel.class);
    }

    public TodoViewModel getTodoViewModel() {
        return todoViewModel;
    }

    @Override
    public void onBackPressed() {
        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);

        if (fragment instanceof HomeFragment) {
            HomeFragment homeFragment = ((HomeFragment) fragment);
            int scrollY = homeFragment.getScrollYPos();

            if (fragment.isVisible() && scrollY > 0 && backstackCount == 0) {
                homeFragment.goToTop();
                return;
            }
        } else if (fragment instanceof CategoriesFragment) {
            CategoriesFragment categoriesFragment = ((CategoriesFragment) fragment);
            int scrollY = categoriesFragment.getScrollYPos();

            if (fragment.isVisible() && scrollY > 0) {
                categoriesFragment.goToTop();
                return;
            }
        }

        super.onBackPressed();
    }
}