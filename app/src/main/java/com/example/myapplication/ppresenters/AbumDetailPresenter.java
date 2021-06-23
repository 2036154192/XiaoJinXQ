package com.example.myapplication.ppresenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.myapplication.data.XimalayApi;
import com.example.myapplication.interfaces.IAlbumDetailBiewCallback;
import com.example.myapplication.interfaces.IAlbumDetialPresenter;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

public class AbumDetailPresenter implements IAlbumDetialPresenter {

    private List<IAlbumDetailBiewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();
    private static final String TAG = "AbumDetailPresenter";

    private Album mTargetAlbum = null;
    private int mCurrentAlbumId = -1;
    private int mCurrentPageIndex = 0;
    private XimalayApi mXimalayApi;

    private AbumDetailPresenter(){}

    private static AbumDetailPresenter sInstance = null;

    public static AbumDetailPresenter getInstance(){
        if (sInstance == null){
            synchronized (AbumDetailPresenter.class){
                if (sInstance == null){
                    sInstance = new AbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {
        mTracks.clear();
        mXimalayApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable @org.jetbrains.annotations.Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    mTracks.addAll(tracks);
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: "+i+"---"+s);
                handlerError(i,s);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

    @Override
    public void loadMore() {
        //加载更多
        mCurrentPageIndex++;
        doLoaded(true);
    }

    private void doLoaded(boolean isLoaderMore){
        mXimalayApi = XimalayApi.getXimalayApi();
        mXimalayApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable @org.jetbrains.annotations.Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    if (isLoaderMore) {
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        Log.d(TAG, "onSuccess: "+size);
                        handlerLoaderMoreResult(size);
                    }else {
                        mTracks.addAll(tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: "+i+"---"+s);
                if (isLoaderMore) {
                    mCurrentPageIndex--;
                }
                handlerError(i,s);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

    //处理加载更多结果
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailBiewCallback callback : mCallbacks) {
            Log.d(TAG, "handlerLoaderMoreResult: "+callback.toString());
            Log.d(TAG, "handlerLoaderMoreResult: "+size);
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetale(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        doLoaded(false);
    }

    private void handlerError(int i, String s) {
        for (IAlbumDetailBiewCallback callback : mCallbacks) {
            callback.onNetworkError(i,s);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailBiewCallback callback : mCallbacks) {
            callback.onDetailListloaded(tracks);
        }
    }

    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum = targetAlbum;
    }

    @Override
    public void reqisterViewCallBack(IAlbumDetailBiewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)){
            mCallbacks.add(callback);
            if (mTargetAlbum != null) {
                callback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallBack(IAlbumDetailBiewCallback callback) {
        mCallbacks.remove(callback);
    }
}
