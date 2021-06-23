package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasePresnter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

//订阅有上限不能超过100个

public interface ISubscriptionPreesenter extends IBasePresnter<ISubscriptionCallback> {

    //添加订阅
    void addSubscription(Album album);

    //删除订阅
    void deleteSubscription(Album album);

    //获取订阅列表
    void getSubscription();

    //判断当前专辑是否以及被收藏
    boolean isSub(Album album);

    void clearSubscription();

}
