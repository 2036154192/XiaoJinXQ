package com.example.myapplication.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

public interface IsubDao {

    void setCallback(IsubDaoCallback callback);

    //增删查
    void addAlbum(Album album);

    void deleteAlbum(Album album);

    void clearAll();

    void ListAlbum();

}
