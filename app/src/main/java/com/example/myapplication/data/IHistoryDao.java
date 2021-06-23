package com.example.myapplication.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {

    //
    void setCallback(IHistoryDaoCallback callback);

    void addHistory(Track track);

    void delHistory(Track track);

    void clearHistory();

    void listHistories();

}
