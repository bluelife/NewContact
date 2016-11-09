package com.oschina.bluelife.newcontact.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oschina.bluelife.newcontact.model.OrgLogo;
import com.oschina.bluelife.newcontact.model.OrgLogoModel;
import com.squareup.sqldelight.SqlDelightStatement;

/**
 * Created by slomka.jin on 2016/11/7.
 */

public class LogoManager {
    private final OrgLogoModel.Update_logo updateLogo;
    private final OrgLogoModel.Insert_row insertRow;
    private final SQLiteOpenHelper sqLiteOpenHelper;

    public LogoManager(Context context){
        sqLiteOpenHelper=LogoOpenHelper.getInstance(context);
        updateLogo=new OrgLogoModel.Update_logo(sqLiteOpenHelper.getWritableDatabase());
        insertRow=new OrgLogoModel.Insert_row(sqLiteOpenHelper.getWritableDatabase());
    }
    public void insert(long id,String image,String name){
        insertRow.bind(id,image,name);
        insertRow.program.executeInsert();
        Log.w("rrrrr",sqLiteOpenHelper.getWritableDatabase().getPath());
    }
    public void update(String image,long id){
        updateLogo.bind(image,id);
        //executeupdateanddelete not avaiable in api 10.
        updateLogo.program.execute();
    }
    public Cursor query(long id){
        SqlDelightStatement query= OrgLogo.FACTORY.select_Logo(id);
        Cursor cursor=sqLiteOpenHelper.getWritableDatabase().rawQuery(query.statement,query.args);
        return cursor;
    }
}
