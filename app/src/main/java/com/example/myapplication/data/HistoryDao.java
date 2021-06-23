package com.example.myapplication.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class HistoryDao implements IHistoryDao{

    private IHistoryDaoCallback mCallback = null;
    private final XiaoJinDBHelper mDbHelper;

    private Object mLock = new Object();

    public HistoryDao(){
        mDbHelper = new XiaoJinDBHelper(BaseApplication.getmContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock){
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDbHelper.getWritableDatabase();
            db.delete(Constants.HISTO_TB_NAME, Constants.HISTO_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(Constants.HISTO_TRACK_ID,track.getDataId());
            values.put(Constants.HISTO_TITLE,track.getTrackTitle());
            values.put(Constants.HISTO_PLAY_CONT,track.getPlayCount());
            values.put(Constants.HISTO_DURATION,track.getDuration());
            values.put(Constants.HISTO_UP_TIME,track.getUpdatedAt());
            values.put(Constants.HISTO_COVER,track.getCoverUrlLarge());
            values.put(Constants.HISTO_AUTHOR,track.getAnnouncer().getNickname());
            db.insert(Constants.HISTO_TB_NAME,null,values);
            db.setTransactionSuccessful();
            isSuccess = true;
        }catch (Exception e){
            isSuccess = false;
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryAdd(isSuccess);
            }
        }
    }
    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isOk = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constants.HISTO_TB_NAME, Constants.HISTO_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                db.setTransactionSuccessful();
                isOk = true;
            } catch (Exception e) {
                e.printStackTrace();
                isOk = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryDel(isOk);
                }
            }
        }
    }
    @Override
    public void clearHistory() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isOk = false;
            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Constants.HISTO_TB_NAME, null, null);
                db.setTransactionSuccessful();
                isOk = true;
            } catch (Exception e) {
                e.printStackTrace();
                isOk = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoryClear(isOk);
                }
            }
        }
    }
    @Override
    public void listHistories() {
        synchronized (mLock){
        SQLiteDatabase db = null;
        List<Track> histories = new ArrayList<>();
        try {
            db = mDbHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.HISTO_TB_NAME, null, null, null, null, null, Constants.HISTO_ID+" desc");
            while (query.moveToNext()) {
                Track track = new Track();
                int trackId = query.getInt(query.getColumnIndex(Constants.HISTO_ID));
                track.setDataId(trackId);
                String title = query.getString(query.getColumnIndex(Constants.HISTO_TITLE));
                track.setTrackTitle(title);
                int playCont = query.getInt(query.getColumnIndex(Constants.HISTO_PLAY_CONT));
                track.setPlayCount(playCont);
                int duration = query.getInt(query.getColumnIndex(Constants.HISTO_DURATION));
                track.setDuration(duration);
                long updatetime = query.getLong(query.getColumnIndex(Constants.HISTO_UP_TIME));
                track.setUpdatedAt(updatetime);
                String corver = query.getString(query.getColumnIndex(Constants.HISTO_COVER));
                track.setCoverUrlLarge(corver);
                String author = query.getString(query.getColumnIndex(Constants.HISTO_AUTHOR));
                Announcer announcer = new Announcer();
                announcer.setNickname(author);
                track.setAnnouncer(announcer);
                histories.add(track);
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryLoaded(histories);
            }
        }
    }
}

}
