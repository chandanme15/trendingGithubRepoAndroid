package com.project.trendGithubRepo.userinterface.main;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.project.trendGithubRepo.Application;
import com.project.trendGithubRepo.R;
import com.project.trendGithubRepo.data.manager.DataManager;
import com.project.trendGithubRepo.userinterface.base.BaseFragment;
import com.project.trendGithubRepo.utils.Constants;
import com.project.trendGithubRepo.utils.Util;

public class MainFragment extends BaseFragment<MainViewModel> implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View mView;
    private MainAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Button button;

    public static MainFragment getInstance() {
        return new MainFragment();
    }

    @Override
    public MainViewModel getViewModel() {
        MainViewModelFactory factory = new MainViewModelFactory(DataManager.getInstance(Application.getInstance()));
        return new ViewModelProvider(getActivity(), factory).get(MainViewModel.class);
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
        showLoading(true);
        viewModel.getRepos().observe(this, itemModels -> {
            mAdapter.clearData();
            mAdapter.addData(itemModels);
            updateRefreshLayout(false);
        });
        viewModel.getError().observe(this, isError -> {
            if(isError) {
                displaySnackbar(true, "Can't load more github repos");
                updateRefreshLayout(false);
            }
        });

        if (DataManager.getInstance(Application.getInstance()).getDate() == null )
            DataManager.getInstance(Application.getInstance()).setDate(Util.getDefaultDate());

        mSwipeRefreshLayout.setRefreshing(false);
        //displaySnackbar(false,"Loading...");
        viewModel.loadRepos(DataManager.getInstance(Application.getInstance()).getDate());

    }

    private void initView(){
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.black);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        button = (Button) getActivity().findViewById(R.id.sample_main_layout).findViewById(R.id.retry_button);
        button.setOnClickListener(v -> {
            retry();
        });
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

    public void retry() {
        Constants.PAGE_COUNT = 1;
        if (Util.isNetworkAvailable(Application.getInstance())){
            showError(View.GONE);
            showLoading(true);
            viewModel.loadRepos(DataManager.getInstance(Application.getInstance()).getDate());
        }
        else {
            mAdapter.clearData();
            showError(View.VISIBLE);
        }
    }

    public DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

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
        if(Util.isNetworkAvailable(Application.getInstance())){
            showError(View.GONE);
            //displaySnackbar(false,"Loading...");
            viewModel.loadRepos(DataManager.getInstance(Application.getInstance()).getDate());
        }else {
            mAdapter.clearData();
            updateRefreshLayout(false);
            showError(View.VISIBLE);
            //displaySnackbar(true,"No Internet Connection :(");
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
        /*getActivity().findViewById(R.id.sample_main_layout).findViewById(R.id.err_mssg_1).setVisibility(Visibility);
        getActivity().findViewById(R.id.sample_main_layout).findViewById(R.id.err_mssg_2).setVisibility(Visibility);*/
    }

    private void showLoading(boolean Visibility){
        getActivity().findViewById(R.id.sample_main_layout).findViewById(R.id.loading).setVisibility(Visibility ? View.VISIBLE : View.GONE );
    }

    private void displaySnackbar(boolean isError, String message){
        Util.showSnack(mView, isError, message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.onClear();
    }

}



