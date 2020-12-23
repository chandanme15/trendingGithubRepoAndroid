package com.project.trendGithubRepo.data.manager;

import android.content.Context;

import java.util.Map;

import com.project.trendGithubRepo.data.helper.SharedPreferencesHelper;
import com.project.trendGithubRepo.data.model.RepoModel;

import io.reactivex.Observable;

public class DataManager {
    private static DataManager mInstance = null;
    private RemoteManager remoteManager;
    private SharedPreferencesHelper sharedPreferencesHelper;


    private DataManager(Context context){
        remoteManager = RemoteManager.getInstance();
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
    }

    public static synchronized DataManager getInstance(Context context){
        return mInstance == null ? mInstance = new DataManager(context) : mInstance;
    }

    public void setDate(String date){
        sharedPreferencesHelper.setDate(date);
    }

    public String getDate(){
        return sharedPreferencesHelper.getDate();
    }

    public Observable<RepoModel> getRepos(Map<String, String> map){
        return remoteManager.getRepos(map);
    }
}
