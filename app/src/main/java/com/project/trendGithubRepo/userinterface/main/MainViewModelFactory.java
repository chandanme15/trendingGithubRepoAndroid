package com.project.trendGithubRepo.userinterface.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.project.trendGithubRepo.data.manager.DataManager;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final DataManager dataManager;

    MainViewModelFactory(DataManager dataManager){
        this.dataManager = dataManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if ( modelClass.isAssignableFrom(MainViewModel.class)){
            return (T) new MainViewModel(dataManager);
        }
        throw new IllegalArgumentException("Unknown view model class");
    }
}
