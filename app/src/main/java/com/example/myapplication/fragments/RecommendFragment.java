package com.example.myapplication.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.Activity.DetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.RecommendListAdapter;
import com.example.myapplication.base.BaseFragment;
import com.example.myapplication.interfaces.IRecommendViewCallback;
import com.example.myapplication.ppresenters.AbumDetailPresenter;
import com.example.myapplication.ppresenters.RecommendPresenter;
import com.example.myapplication.utils.UIUtil;
import com.example.myapplication.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;


import org.jetbrains.annotations.NotNull;

import java.util.List;


public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {

    private String TAG ="RecommendFragment";
    private RecyclerView mRecyclerView;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private View mRootView;
    private UILoader mUiLoader;
    private TwinklingRefreshLayout mTwinkling;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {

        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        mRecommendPresenter = RecommendPresenter.getInstance();
        mRecommendPresenter.reqisterViewCallBack(this);
        mRecommendPresenter.getRecommendList();

        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                //网路不佳点击
                if (mRecommendPresenter != null) {
                    mRecommendPresenter.getRecommendList();
                }
            }
        });

        return mUiLoader;
    }
    private boolean isLoaderMore = false;

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend2, container, false);
        mRecyclerView = mRootView.findViewById(R.id.recommand_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull @NotNull Rect outRect, @NonNull @NotNull View view, @NonNull @NotNull RecyclerView parent, @NonNull @NotNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecommendListAdapter = new RecommendListAdapter();
        mRecyclerView.setAdapter(mRecommendListAdapter);

        mRecommendListAdapter.setonRecomendItemClickLister(new RecommendListAdapter.onRecomendItemClickLister() {
            @Override
            public void onItemClick(int postion, Album album) {
                AbumDetailPresenter.getInstance().setTargetAlbum(album);
                //列表点击事件
                Intent intent = new Intent(getContext(), DetailActivity.class);
                startActivity(intent);
            }
        });

        mTwinkling = mRootView.findViewById(R.id.twinkling);
        mTwinkling.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                if (mRecommendPresenter != null) {
                    mRecommendPresenter.pull2RefreshMore();
                    mTwinkling.finishRefreshing();
                    Toast.makeText(getContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (mRecommendPresenter != null) {
                    mRecommendPresenter.loadMore();
                    isLoaderMore = true;
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallBack(this);
        }
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {

        if (isLoaderMore && mRecommendPresenter != null) {
            mTwinkling.finishLoadmore();
            isLoaderMore =false;
        }

        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onNetworkError() {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onLoaderMoreFinishedf(int size) {
        if (size>0){
            Toast.makeText(getContext(), "加载成功", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消注册接口，避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallBack(this);
        }
    }
}