package com.elaine;

import android.app.Application;

import com.elaine.okretrolib.*;
import com.facebook.stetho.Stetho;

/**
 * Created by elaine on 2016/11/17.
 */

public class ElaineApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        RequestUtils.init(this, BuildConfig.DEBUG);
    }
}
