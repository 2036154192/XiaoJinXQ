package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    //搜索结果的回调
    void onSearchResultLoaded(List<Album> result);

    //获取推荐热词的结果的回调
    void onHotWordLoaded(List<HotWord> hotWordList);

    //加载更多的结果返回
    void onLoadMoreResult(List<Album> result,boolean isOkay);

    //联想关键词推荐
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    //错误通知
    void onError(int i,String s);

}
