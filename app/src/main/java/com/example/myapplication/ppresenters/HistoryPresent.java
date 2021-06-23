package com.example.myapplication.ppresenters;

import android.util.Log;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.data.HistoryDao;
import com.example.myapplication.data.IHistoryDao;
import com.example.myapplication.data.IHistoryDaoCallback;
import com.example.myapplication.interfaces.IHistoryCallback;
import com.example.myapplication.interfaces.IHistoryPresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryPresent implements IHistoryPresenter, IHistoryDaoCallback {

    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private static final String TAG = "HistoryPresent";
    private final IHistoryDao mHistoryDao;
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;

    private HistoryPresent(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    private static HistoryPresent sHistoryPresent = null;

    public static HistoryPresent getHistoryPresent(){
        if (sHistoryPresent == null) {
            synchronized (HistoryPresent.class){
                sHistoryPresent = new HistoryPresent();
            }
        }
        return sHistoryPresent;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isOutOfSize = false;

    @Override
    public void addHistory(Track track) {
        //设定最大保存值
        if (mCurrentHistories != null && mCurrentHistories.size() >= 100) {
            isOutOfSize = true;
            this.mCurrentAddTrack = track;
            //先删除一条最后的记录，在添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
        }else {
            doAddHistroy(track);
        }
    }

    private void doAddHistroy(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void reqisterViewCallBack(IHistoryCallback callback) {
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallBack(IHistoryCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        if (isOutOfSize && mCurrentAddTrack != null) {
            //添加当前的数据进到数据库
            isOutOfSize = false;
            addHistory(mCurrentAddTrack);
        }else {
            listHistories();
        }
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        this.mCurrentHistories = tracks;
        Log.d(TAG, "onHistoryLoaded: "+tracks);
        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoryClear(boolean isSuccess) {
        listHistories();
    }
}
