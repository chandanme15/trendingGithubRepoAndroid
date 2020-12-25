package com.project.trendGithubRepo.utils;

import android.widget.LinearLayout;

import com.project.trendGithubRepo.data.model.ItemModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CacheData implements Serializable {

    private String time;
    private List<ItemModel> data = new ArrayList<>();

    public CacheData() {
        time = "";
    }

    public CacheData(String time, List<ItemModel> data) {
        this.data.addAll(data);
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public List<ItemModel> getData() {
        return data;
    }

}
