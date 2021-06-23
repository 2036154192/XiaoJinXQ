package com.example.myapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.utils.Constants;


public class XiaoJinDBHelper extends SQLiteOpenHelper {

    public XiaoJinDBHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String subTbSql = "create table "+Constants.DY_TB_NAME+" ("+Constants.DY_ID+" integer primary key autoincrement,"+Constants.DY_COVER_URL+" varchar," +
                ""+Constants.DY_TITLE+" varchar,"+Constants.DY_DESCRIPTION+" varchar,"+Constants.DY_PLAYCOUNT+" integer," +
                ""+Constants.DY_TRACKSCOUNT+" integer,"+Constants.DY_AUTHORNAME+" varchar,"+Constants.DY_ALBUMID+" integer)";
        db.execSQL(subTbSql);
        String histoTbSql = "create table "+Constants.HISTO_TB_NAME+" ("
                +Constants.HISTO_ID+" integer primary key autoincrement,"
                +Constants.HISTO_TRACK_ID+" integer,"
                +Constants.HISTO_TITLE+" varchar,"
                +Constants.HISTO_COVER+" varchar,"
                +Constants.HISTO_PLAY_CONT+" integer,"
                +Constants.HISTO_DURATION+" integer,"
                +Constants.HISTO_AUTHOR+" varchar,"
                +Constants.HISTO_UP_TIME+" integer)";
        db.execSQL(histoTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
