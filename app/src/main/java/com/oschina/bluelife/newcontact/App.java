package com.oschina.bluelife.newcontact;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.oschina.bluelife.newcontact.model.ContactSource;

/**
 * Created by HiWin10 on 2016/10/23.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

    }
}
