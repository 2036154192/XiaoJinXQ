package com.example.myapplication.ppresenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.myapplication.data.XimalayApi;
import com.example.myapplication.interfaces.IRecommendPresenter;
import com.example.myapplication.interfaces.IRecommendViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private List<IRecommendViewCallback> mIRecommendViewCallbacks = new ArrayList<>();
    private static final String TAG = "RecommendPresenter";
    private static int YESHU = 1;
    private List<Album> mTracks = new ArrayList<>();
    private XimalayApi mXimalayApi;
    private List<Album> mCurrentRecommend = null;

    //懒汉式单利对象
    private RecommendPresenter(){}

    private static RecommendPresenter sInstance = null;

    public static RecommendPresenter getInstance(){
        if (sInstance == null){
            synchronized (RecommendPresenter.class){
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    private void handlerRecommendResult(List<Album> albums) {
        //通知UI更新
        if (albums != null) {
            if (albums.size() == 0) {
                for (IRecommendViewCallback iRecommendViewCallback : mIRecommendViewCallbacks) {
                    iRecommendViewCallback.onEmpty();
                }
            }else {
                for (IRecommendViewCallback iRecommendViewCallback : mIRecommendViewCallbacks) {
                    iRecommendViewCallback.onRecommendListLoaded(albums);
                }
                this.mCurrentRecommend = albums;
            }
        }
    }

    private void updateLoading(){
        for (IRecommendViewCallback iRecommendViewCallback : mIRecommendViewCallbacks) {
            iRecommendViewCallback.onLoading();
        }
    }

    @Override
    public void getRecommendList() {
        aLoadMore();
    }

    private void aLoadMore() {
        updateLoading();
        mXimalayApi = XimalayApi.getXimalayApi();
        mXimalayApi.getRecommendList(new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable @org.jetbrains.annotations.Nullable SearchAlbumList searchAlbumList) {
                if (searchAlbumList != null) {
                    List<Album> albums = searchAlbumList.getAlbums();
                    mTracks.addAll(albums);
                    handlerRecommendResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: "+i+"----"+s);
                handlerError();
            }
        },YESHU);
    }

    private void handlerLoaderMoreResult(int size) {
        for (IRecommendViewCallback iRecommendViewCallback : mIRecommendViewCallbacks) {
            iRecommendViewCallback.onLoaderMoreFinishedf(size);
        }
    }

    private void handlerError() {
        for (IRecommendViewCallback iRecommendViewCallback : mIRecommendViewCallbacks) {
            iRecommendViewCallback.onNetworkError();
        }
    }

    @Override
    public void pull2RefreshMore() {
        YESHU = 1;
        mTracks.clear();
        aLoadMore();
    }

    @Override
    public void loadMore() {
        YESHU++;
        mXimalayApi.getRecommendList(new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable @org.jetbrains.annotations.Nullable SearchAlbumList searchAlbumList) {
                if (searchAlbumList != null) {
                    List<Album> albums = searchAlbumList.getAlbums();
                    mTracks.addAll(albums);
                    int size = albums.size();
                    handlerLoaderMoreResult(size);
                    handlerRecommendResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: "+i+"----"+s);
                YESHU--;
                handlerError();
            }
        },YESHU);
    }

    @Override
    public void reqisterViewCallBack(IRecommendViewCallback callback) {
        if (mIRecommendViewCallbacks!=null && !mIRecommendViewCallbacks.contains(callback)) {
            mIRecommendViewCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallBack(IRecommendViewCallback callback) {
        if (mIRecommendViewCallbacks !=null){
            mIRecommendViewCallbacks.remove(callback);
        }
    }
}
