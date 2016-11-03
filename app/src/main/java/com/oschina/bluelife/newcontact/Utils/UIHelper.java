package com.oschina.bluelife.newcontact.Utils;


import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by slomka.jin on 2016/10/20.
 */

public class UIHelper {
    public static TextView getToolbarTitleView(AppCompatActivity activity, Toolbar toolbar){
        ActionBar actionBar = activity.getSupportActionBar();
        CharSequence actionbarTitle = null;
        if(actionBar != null)
            actionbarTitle = actionBar.getTitle();
        actionbarTitle = TextUtils.isEmpty(actionbarTitle) ? toolbar.getTitle() : actionbarTitle;
        if(TextUtils.isEmpty(actionbarTitle)) return null;
        // can't find if title not set
        for(int i= 0; i < toolbar.getChildCount(); i++){
            View v = toolbar.getChildAt(i);
            if(v != null && v instanceof TextView){
                TextView t = (TextView) v;
                CharSequence title = t.getText();
                if(!TextUtils.isEmpty(title) && actionbarTitle.equals(title) && t.getId() == View.NO_ID){
                    //Toolbar does not assign id to views with layout params SYSTEM, hence getId() == View.NO_ID
                    //in same manner subtitle TextView can be obtained.
                    return t;
                }
            }
        }
        return null;
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static int getMaxContactPhotoSize(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Note that this URI is safe to call on the UI thread.
            final Uri uri = ContactsContract.DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI;
            final String[] projection = new String[] { ContactsContract.DisplayPhoto.DISPLAY_MAX_DIM };
            final Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
            try {
                c.moveToFirst();
                return c.getInt(0);
            } finally {
                c.close();
            }
        }
        // fallback: 96x96 is the max contact photo size for pre-ICS versions
        return 96;
    }
}
