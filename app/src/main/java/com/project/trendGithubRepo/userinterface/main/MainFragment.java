package com.project.trendGithubRepo.userinterface.main;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.project.trendGithubRepo.Application;
import com.project.trendGithubRepo.R;
import com.project.trendGithubRepo.data.manager.DataManager;
import com.project.trendGithubRepo.userinterface.base.BaseFragment;
import com.project.trendGithubRepo.utils.CacheData;
import com.project.trendGithubRepo.utils.CacheTime;
import com.project.trendGithubRepo.utils.Constants;
import com.project.trendGithubRepo.utils.FileSystem;
import com.project.trendGithubRepo.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends BaseFragment<MainViewModel> implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String cacheFileData = "GihubRepoCacheData.txt";
    public static final String cacheFileTime = "GihubRepoCacheTime.txt";

    private View mView;
    private MainAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Button button;
    private boolean m_bIsGithubRepoLoading = false;

    static MainFragment getInstance() {
        return new MainFragment();
    }

    @Override
    public MainViewModel getViewModel() {
        MainViewModelFactory factory = new MainViewModelFactory(DataManager.getInstance(Application.getInstance()));
        return new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this,mView);
        initView();
        setupRecycler();

        return mView;
    }

    @Override
    public void onStart () {
        super.onStart();
        m_bIsGithubRepoLoading = false;
        Constants.PAGE_COUNT = 1;
        showLoading(true);
        showError(View.GONE);
        viewModel.getRepos().observe(this, itemModels -> {
            if(Constants.PAGE_COUNT == 1) {
                mAdapter.clearData();
            }
            mAdapter.addData(itemModels);
            updateRefreshLayout(false);
            saveDataInCache();
        });
        viewModel.getError().observe(this, isError -> {
            if(isError) {
                //displaySnackbar(true, "Can't load github repos");
                if(!bIsLoadDataFromCacheSuccesful()) {
                    showError(View.VISIBLE);
                }
                updateRefreshLayout(false);
            }
        });

        if (DataManager.getInstance(Application.getInstance()).getDate() == null )
            DataManager.getInstance(Application.getInstance()).setDate(Util.getDefaultDate());

        if(LoadData()) {
            showError(View.GONE);
            showLoading(m_bIsGithubRepoLoading);
        }
        else {
            showError(View.VISIBLE);
            showLoading(false);
        }
    }

    private boolean LoadData() {
        if(!bIsCacheExpired()) {
            m_bIsGithubRepoLoading = false;
            return bIsLoadDataFromCacheSuccesful();
        }
        else if (Util.isNetworkAvailable(Application.getInstance())) {
            LoadReposFromGithub();
            m_bIsGithubRepoLoading = true;
            return true;
        }
        m_bIsGithubRepoLoading = false;
        return bIsLoadDataFromCacheSuccesful();
    }

    private void LoadReposFromGithub() {
        viewModel.loadRepos(DataManager.getInstance(Application.getInstance()).getDate());
    }


    private void onMenuClicked(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        popup.getMenuInflater().inflate(R.menu.menu_bar, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_sort_by_stars:
                    mAdapter.sortDataByStars();
                    break;
                case R.id.menu_sort_by_names:
                    mAdapter.sortDataByNames();
                    break;
            }
            return true;
        });
        popup.show();
    }

    private void initView(){
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getActivity().findViewById(R.id.retry_button).setOnClickListener(this::retry);
        getActivity().findViewById(R.id.img_item_menu).setOnClickListener(this::onMenuClicked);
    }

    private void setupRecycler(){
        mLayoutManager = new LinearLayoutManager(Application.getInstance());
        mAdapter = new MainAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        int scrollPosition = 0;
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(Application.getInstance(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,int newState) {
            int totalItemCount = mLayoutManager.getItemCount();
            int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

            if (totalItemCount> 1 && lastVisibleItem >= totalItemCount - 1 )
            {
                if (Util.isNetworkAvailable(Application.getInstance())){
                    Constants.PAGE_COUNT++;
                    displaySnackbar(false, "Loading...");
                    viewModel.loadRepos(DataManager.getInstance(Application.getInstance()).getDate());
                }
                else
                    displaySnackbar(true,"No internet Connection ! ");
            }
        }
    };

    @Override
    public void onClick(View view) {
        new DatePickerDialog(view.getContext(), date,
                Util.getYear(DataManager.getInstance(Application.getInstance()).getDate()),
                Util.getMonth(DataManager.getInstance(Application.getInstance()).getDate())-1,
                Util.getDay(DataManager.getInstance(Application.getInstance()).getDate())).show();
    }

    private void retry(View view) {
        Constants.PAGE_COUNT = 1;
        showLoading(true);
        showError(View.GONE);
        if (Util.isNetworkAvailable(Application.getInstance())) {
            LoadReposFromGithub();
        }
        else if(LoadData()) {
            updateRefreshLayout(false);
        }
        else {
            showError(View.VISIBLE);
            updateRefreshLayout(false);
        }
    }

    private DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

        DataManager.getInstance(Application.getInstance()).setDate(Util.formatDate(year,monthOfYear+1,dayOfMonth));

        mAdapter.clearData();
        Constants.PAGE_COUNT = 1;

        if(Util.isNetworkAvailable(Application.getInstance())){
            showError(View.GONE);
            displaySnackbar(false,"Loading...");
            viewModel.loadRepos(DataManager.getInstance(Application.getInstance()).getDate());
        }
        else {
            showError(View.VISIBLE);
            displaySnackbar(true,"No Internet Connection :(");
        }
    };

    @Override
    public void onRefresh() {
        Constants.PAGE_COUNT = 1;
        updateRefreshLayout(true);
        if (Util.isNetworkAvailable(Application.getInstance())) {
            LoadReposFromGithub();
        }
        else if(LoadData()) {
            showError(View.GONE);
            updateRefreshLayout(false);
        }
        else {
            showError(View.VISIBLE);
            updateRefreshLayout(false);
        }
    }

    private void updateRefreshLayout(boolean refresh) {
        if(!refresh) {
            showLoading(refresh);
        }
        mSwipeRefreshLayout.setRefreshing(refresh);
    }

    private void showError(int Visibility){
        getActivity().findViewById(R.id.sample_main_layout).findViewById(R.id.error).setVisibility(Visibility);
        getActivity().findViewById(R.id.sample_main_layout).findViewById(R.id.retry_button).setVisibility(Visibility);
    }

    private void showLoading(boolean Visibility){
        getActivity().findViewById(R.id.sample_main_layout).findViewById(R.id.loading).setVisibility(Visibility ? View.VISIBLE : View.GONE );
    }

    private void displaySnackbar(boolean isError, String message){
        Util.showSnack(mView, isError, message);
    }

    private boolean bIsCacheExpired() {
        try {
            long cacheTime = Long.parseLong(FileSystem.ReadFromFile(getActivity(), cacheFileTime).replaceAll("[\\D+]", ""));
            long currentTime = Long.parseLong(Util.getCurrentDateAndTime());
            return (currentTime - cacheTime) > 20000;
        }
        catch (Exception e) {

        }
        return true;
    }


    private boolean purgeCacheData() {
        FileSystem.DeleteFile(getActivity(), cacheFileData);
        return FileSystem.DeleteFile(getActivity(), cacheFileTime);
    }

    private boolean saveDataInCache() {
        try {
            purgeCacheData();
            List<CacheData> cacheDataArrayList = new ArrayList<>();
            cacheDataArrayList.add(new CacheData(Util.getCurrentDateAndTime(), mAdapter.getData()));
            boolean bRetData = FileSystem.ReWriteFile(getActivity(), cacheFileData, cacheDataArrayList);

            List<CacheTime> t = new ArrayList<>();
            t.add(new CacheTime(Long.parseLong(Util.getCurrentDateAndTime())));
            boolean bRetTime = FileSystem.ReWriteFile(getActivity(), cacheFileTime, t);
            return bRetData & bRetTime;
        } catch (Exception e) {

        }
        return false;
    }

    private boolean bIsLoadDataFromCacheSuccesful() {
        try {
            List<CacheData> cacheData = FileSystem.ReadFile(getActivity(), CacheData[].class, cacheFileData);
            if (cacheData == null || cacheData.size() == 0) {
                return false;
            }
            boolean bRet = mAdapter.addData(cacheData.get(0).getData());
            if(bRet) {
                displaySnackbar(false, "Showing Cached Data");
            }
            return bRet;
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onClear();
    }

}



