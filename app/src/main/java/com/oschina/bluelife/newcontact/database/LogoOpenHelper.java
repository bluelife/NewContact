package com.oschina.bluelife.newcontact.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oschina.bluelife.newcontact.model.OrgLogo;

/**
 * Created by slomka.jin on 2016/11/7.
 */

public class LogoOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static LogoOpenHelper instance;

    public static LogoOpenHelper getInstance(Context context){
        if(instance==null){
            instance=new LogoOpenHelper(context);
        }
        return instance;
    }
    public LogoOpenHelper(Context context) {
        super(context, "logo", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OrgLogo.CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
