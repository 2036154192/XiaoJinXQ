package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasePresnter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryPresenter extends IBasePresnter<IHistoryCallback> {

    //获取历史内容
    void listHistories();

    //添加历史
    void addHistory(Track track);

    //删除历史
    void delHistory(Track track);

    //清空历史
    void clearHistory();

}
