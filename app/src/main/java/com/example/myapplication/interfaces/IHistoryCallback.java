package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback {

    //历史内容加载
    void onHistoriesLoaded(List<Track> tracks);


}
