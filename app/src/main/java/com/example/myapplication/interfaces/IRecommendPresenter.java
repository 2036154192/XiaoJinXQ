package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasePresnter;

public interface IRecommendPresenter extends IBasePresnter<IRecommendViewCallback> {

    //获取推荐内容
    void getRecommendList();

    //下拉刷新
    void pull2RefreshMore();

    //上拉加载
    void loadMore();

}
