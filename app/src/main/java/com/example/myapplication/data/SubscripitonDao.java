package com.example.myapplication.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.base.BaseApplication;
import com.example.myapplication.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscripitonDao implements IsubDao {

    private static final String TAG = "SubscripitonDao";

    private static final SubscripitonDao ourInstance = new SubscripitonDao();
    private final XiaoJinDBHelper mXiaoJinDBHelper;
    private IsubDaoCallback mCallback = null;

    private Object mLock = new Object();

    public static SubscripitonDao getInstance(){
        return ourInstance;
    }

    private SubscripitonDao(){
        mXiaoJinDBHelper = new XiaoJinDBHelper(BaseApplication.getmContext());
    }

    @Override
    public void setCallback(IsubDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addAlbum(Album album) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isAddSuccess = false;
            try {
                db = mXiaoJinDBHelper.getWritableDatabase();
                db.beginTransaction();

                ContentValues contentValues = new ContentValues();
                contentValues.put(Constants.DY_COVER_URL, album.getCoverUrlLarge());
                contentValues.put(Constants.DY_TITLE, album.getAlbumTitle());
                contentValues.put(Constants.DY_DESCRIPTION, album.getAlbumIntro());
                contentValues.put(Constants.DY_TRACKSCOUNT, album.getIncludeTrackCount());
                contentValues.put(Constants.DY_PLAYCOUNT, album.getPlayCount());
                contentValues.put(Constants.DY_AUTHORNAME, album.getAnnouncer().getNickname());
                contentValues.put(Constants.DY_ALBUMID, album.getId());

                db.insert(Constants.DY_TB_NAME, null, contentValues);
                db.setTransactionSuccessful();
                isAddSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isAddSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onAddResult(isAddSuccess);
                }
            }
        }
    }
    @Override
    public void deleteAlbum(Album album) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isOk = false;
            try {
                db = mXiaoJinDBHelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constants.DY_TB_NAME, Constants.DY_ALBUMID + "=?", new String[]{album.getId() + ""});
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
                    mCallback.onDeleteResult(isOk);
                }
            }
        }
    }

    @Override
    public void clearAll() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isOk = false;
            try {
                db = mXiaoJinDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Constants.DY_TB_NAME,null,null);
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
                    mCallback.onSubClearAll(isOk);
                }
            }
        }
    }

    @Override
    public void ListAlbum() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            List<Album> result = new ArrayList<>();
            try {
                db = mXiaoJinDBHelper.getReadableDatabase();
                db.beginTransaction();
                Cursor query = db.query(Constants.DY_TB_NAME, null, null, null, null, null, Constants.DY_ID + " desc");
                while (query.moveToNext()) {
                    Album album = new Album();
                    String coverUrl = query.getString(query.getColumnIndex(Constants.DY_COVER_URL));
                    album.setCoverUrlLarge(coverUrl);
                    String title = query.getString(query.getColumnIndex(Constants.DY_TITLE));
                    album.setAlbumTitle(title);
                    String description = query.getString(query.getColumnIndex(Constants.DY_DESCRIPTION));
                    album.setAlbumIntro(description);
                    int tracksCount = query.getInt(query.getColumnIndex(Constants.DY_TRACKSCOUNT));
                    album.setIncludeTrackCount(tracksCount);
                    int playCount = query.getInt(query.getColumnIndex(Constants.DY_PLAYCOUNT));
                    album.setPlayCount(playCount);
                    int albumId = query.getInt(query.getColumnIndex(Constants.DY_ALBUMID));
                    album.setId(albumId);
                    String aothorName = query.getString(query.getColumnIndex(Constants.DY_AUTHORNAME));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(aothorName);
                    album.setAnnouncer(announcer);
                    result.add(album);
                }
                query.close();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onSubListLoaded(result);
                }
            }
        }
    }
}
