package com.project.trendGithubRepo.utils;

import com.project.trendGithubRepo.data.model.ItemModel;

import java.io.Serializable;
import java.util.List;

public class CacheTime implements Serializable {

    private Long time;

    public CacheTime() {
        time = 0L;
    }

    public CacheTime(long time) {
        this.time = time;
    }

    public long getCacheTime() {
        return time;
    }
}
