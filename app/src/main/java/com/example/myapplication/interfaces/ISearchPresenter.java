package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasePresnter;

public interface ISearchPresenter extends IBasePresnter<ISearchCallback> {

    //搜索
    void doSearch(String keyword);

    //重新搜索
    void reSearch();

    //加载更多的搜索结果
    void loadMore();

    //获取热词
    void getHotWord();

    //推荐列表
    void getRecommendWord(String keyword);

}
