package com.project.trendGithubRepo.userinterface.main;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.trendGithubRepo.data.manager.DataManager;
import com.project.trendGithubRepo.data.model.ItemModel;
import com.project.trendGithubRepo.data.model.RepoModel;
import com.project.trendGithubRepo.userinterface.base.BaseViewModel;
import com.project.trendGithubRepo.utils.Constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {
    private MutableLiveData<List<ItemModel>> repos = new MutableLiveData<>();
    private MutableLiveData<Boolean> error = new MutableLiveData<>();
    private static Map<String, String> map = new HashMap<>();
    private final DataManager dataManager;
    private CompositeDisposable disposable = new CompositeDisposable();

    MainViewModel(DataManager dataManager){
        this.dataManager = dataManager;
    }

    public MutableLiveData<List<ItemModel>> getRepos() {
        return repos;
    }

    public MutableLiveData<Boolean> getError() {
        return error;
    }

    public void loadRepos(String date){
        initMap();
        if( date != null && !date.isEmpty()) map.put("q","created:>"+date);
        disposable.add(
                dataManager.getRepos(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(RepoModel::getItems)
                        .subscribe(itemModels -> {
                            if(itemModels !=null && !itemModels.isEmpty()){
                                repos.postValue(itemModels);
                                error.postValue(false);
                            }
                            }, error -> this.error.postValue(true)
                        )
        );
    }


    private void initMap(){
        map.put("q","created:>");
        map.put("sort","stars");
        map.put("order","desc");
        map.put("page",String.valueOf(Constants.PAGE_COUNT));
    }

    public void onClear(){
        if(disposable != null ) disposable.dispose();
    }

}
