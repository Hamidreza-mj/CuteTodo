package ui.activity;

import android.os.Bundle;

import hlv.cute.todo.R;
import hlv.cute.todo.databinding.ActivityMainBinding;
import utils.ToastHelper;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
    }

    private void initViews() {

    }
}