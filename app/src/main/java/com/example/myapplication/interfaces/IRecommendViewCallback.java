package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallback {

    //获取推荐内容结果
    void onRecommendListLoaded(List<Album> result);

    //加载更多内容
    void onLoaderMore(List<Album> result);

    //下拉刷新
    void onRefreshMore(List<Album> result);

    //网络错误
    void onNetworkError();

    //数据为空
    void onEmpty();

    //正在加载
    void onLoading();

    //加载更多true表示加载成功false加载失败
    void onLoaderMoreFinishedf(int size);
}
