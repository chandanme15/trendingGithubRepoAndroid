package com.project.trendGithubRepo.userinterface.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.project.trendGithubRepo.Application;
import com.project.trendGithubRepo.R;
import com.project.trendGithubRepo.data.manager.DataManager;
import com.project.trendGithubRepo.userinterface.base.BaseActivity;
import com.project.trendGithubRepo.utils.Util;

public class MainActivity extends BaseActivity<MainViewModel> {

    @BindView(R.id.sample_main_layout)
    LinearLayout mainLayout;
    @BindView(R.id.imgview)
    ImageView imageView;

    @Override
    public MainViewModel createViewModel() {
        MainViewModelFactory factory = new MainViewModelFactory(DataManager.getInstance(Application.getInstance()));
        return new ViewModelProvider(this,factory).get(MainViewModel.class);
    }

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.action_bar);
        }
        ButterKnife.bind(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, MainFragment.getInstance()).commit();
    }
}
