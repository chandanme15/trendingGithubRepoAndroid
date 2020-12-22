package com.project.trendGithubRepo.userinterface.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity <T extends BaseViewModel> extends AppCompatActivity {
    protected T viewModel;

    public abstract T createViewModel();

    @Override
    protected void onCreate(final Bundle bundle){
        super.onCreate(bundle);
        viewModel = createViewModel();
    }
}