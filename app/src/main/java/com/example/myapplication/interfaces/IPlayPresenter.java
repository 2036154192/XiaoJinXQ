package com.example.myapplication.interfaces;

import com.example.myapplication.base.IBasePresnter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayPresenter extends IBasePresnter<IPlayerCallback> {

    //播放
    void play();

    //暂停
    void pause();

    //停止
    void stop();

    //上一首
    void playPre();

    //下一首
    void playNext();

    //却换播放模式
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    //获取播放列表
    void getPlayList();

    //列表点击播放
    void playByIndex(int index);

    //切换播放进度
    void seekTo(int progress);

    //判断是否是播放
    boolean isPlay();

    //播放列表反转
    void revesePlayList();


    void playByAlbumId(long id);

}
