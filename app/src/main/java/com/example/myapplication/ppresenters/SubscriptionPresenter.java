package com.example.myapplication.ppresenters;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.data.IsubDaoCallback;
import com.example.myapplication.data.SubscripitonDao;
import com.example.myapplication.interfaces.ISubscriptionCallback;
import com.example.myapplication.interfaces.ISubscriptionPreesenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPreesenter, IsubDaoCallback {

    private final SubscripitonDao mSubscripitonDao;
    private Map<Long,Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    private SubscriptionPresenter(){
        mSubscripitonDao = SubscripitonDao.getInstance();
        mSubscripitonDao.setCallback(this);
    }

    private void listSubscritpions(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscripitonDao != null) {
                    mSubscripitonDao.ListAlbum();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private static SubscriptionPresenter sSubscriptionPresenter = null;

    public static SubscriptionPresenter getInstance(){
        if (sSubscriptionPresenter == null) {
            synchronized (SubscriptionPresenter.class){
                sSubscriptionPresenter = new SubscriptionPresenter();
            }
        }
        return sSubscriptionPresenter;
    }

    @Override
    public void addSubscription(Album album) {
        //判断当前的订阅数量不能超过100个
        if (mData.size() >= 100){
            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubTooMany();
            }
            return;
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscripitonDao != null) {
                    mSubscripitonDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscripitonDao != null) {
                    mSubscripitonDao.deleteAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscription() {
        listSubscritpions();
    }

    @Override
    public boolean isSub(Album album) {
        Album album1 = mData.get(album.getId());
        return album1 != null;
    }

    @Override
    public void clearSubscription() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mSubscripitonDao != null) {
                    mSubscripitonDao.clearAll();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void reqisterViewCallBack(ISubscriptionCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallBack(ISubscriptionCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void onAddResult(boolean isSuccess) {
        listSubscritpions();
        //添加回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        listSubscritpions();
        //删除回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(List<Album> albums) {
        //加载数据回调
        mData.clear();
        for (Album album : albums) {
            mData.put(album.getId(),album);
        }
        //通知UI更新
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscritpionLoadeds(albums);
                }
            }
        });
    }

    @Override
    public void onSubClearAll(boolean isSuccess) {
        listSubscritpions();
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onClearSub();
                }
            }
        });
    }
}
