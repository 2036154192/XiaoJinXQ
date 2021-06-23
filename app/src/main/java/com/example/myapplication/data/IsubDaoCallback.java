package com.example.myapplication.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IsubDaoCallback {

    //添加回调
    void onAddResult(boolean isSuccess);

    //删除
    void onDeleteResult(boolean isSuccess);

    //加载的结果
    void onSubListLoaded(List<Album> albums);

    void onSubClearAll(boolean isSuccess);
}
