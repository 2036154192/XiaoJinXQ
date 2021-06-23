package com.example.myapplication.base;

import com.example.myapplication.interfaces.IRecommendViewCallback;

public interface IBasePresnter<T> {

    //这个方法用于注册UI的回调
    void reqisterViewCallBack(T callback);

    //取消回调
    void unRegisterViewCallBack(T callback);

}
