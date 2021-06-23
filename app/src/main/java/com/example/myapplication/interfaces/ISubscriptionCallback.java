package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {

    //调用添加的时候去通知UI
    void onAddResult(boolean isSuccess);

    //删除
    void onDeleteResult(boolean isSuccess);

    //获取订阅专辑加载结果回调方法
    void onSubscritpionLoadeds(List<Album> albums);

    //
    void onSubTooMany();

    void onClearSub();

}
