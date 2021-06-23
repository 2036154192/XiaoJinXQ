package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasePresnter;

public interface IAlbumDetialPresenter extends IBasePresnter<IAlbumDetailBiewCallback> {

    //下拉刷新
    void pull2RefreshMore();

    //上拉加载
    void loadMore();

    //获取专辑详情
    void getAlbumDetale(int albumId,int page);

}
