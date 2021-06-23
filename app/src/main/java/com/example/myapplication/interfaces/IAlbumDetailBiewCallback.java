package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailBiewCallback {

    //专辑详情内容加载出来了
    void onDetailListloaded(List<Track> tracks);

    //把album传给UI
    void onAlbumLoaded(Album album);

    void onNetworkError(int i, String s);

    //加载更多true表示加载成功false加载失败
    void onLoaderMoreFinished(int size);

    //下拉加载更多
    void onRefreshFinished(int size);

}
