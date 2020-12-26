package com.project.trendGithubRepo.userinterface.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.project.trendGithubRepo.Application;
import com.project.trendGithubRepo.R;
import com.project.trendGithubRepo.data.manager.DataManager;
import com.project.trendGithubRepo.userinterface.base.BaseActivity;

public class MainActivity extends BaseActivity<MainViewModel> {

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
        if(isStoragePermissionGranted()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, MainFragment.getInstance()).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getSupportFragmentManager().beginTransaction().replace(R.id.sample_content_fragment, MainFragment.getInstance()).commit();
        }
        else {
            finish();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}