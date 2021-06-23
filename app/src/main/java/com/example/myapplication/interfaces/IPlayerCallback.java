package com.example.myapplication.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {

    //开始播放
    void onPlayStart();

    //播放暂停
    void onPlayPause();

    //播放停止
    void onPlayStop();

    //播放错误
    void onPlayError();

    //下一首
    void nextPlay(Track track);

    //上一首
    void onPlayPre(Track track);

    //播放列表数据加载完成
    void onListLoaded(List<Track> list);

    //播放模式却换
    void onPlayModeChage(XmPlayListControl.PlayMode mode);

    //播放进度改变
    void onProgressChage(int currentProgress,int total);

    //广工正在加载
    void onAdLoading();

    //广工加载完成
    void onAdFinished();

    //界面信息
    void onTrackUpdate(Track track,int playIdex);

    //通知UI
    void updateListOrder(boolean isReverse);

}