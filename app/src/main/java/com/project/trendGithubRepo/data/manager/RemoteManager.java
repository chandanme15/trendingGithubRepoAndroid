package com.project.trendGithubRepo.data.manager;

import java.util.Map;

import com.project.trendGithubRepo.data.model.RepoModel;
import com.project.trendGithubRepo.data.rest.RestApi;
import com.project.trendGithubRepo.data.rest.RestClient;
import io.reactivex.Observable;

public class RemoteManager {
    private static RemoteManager mInstance = null;

    private RemoteManager(){}

    public static RemoteManager getInstance(){
        return mInstance == null ? mInstance= new RemoteManager() : mInstance;
    }

    public Observable<RepoModel> getRepos(Map<String, String> map){
        RestApi api = RestClient.getApiService();
        return api.getRepos(map);
    }
}
