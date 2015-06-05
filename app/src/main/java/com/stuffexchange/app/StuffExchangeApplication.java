package com.stuffexchange.app;

import android.app.Application;

public class StuffExchangeApplication extends Application {
    private String mUserId;
    private String mToken;
    private DataAccess mDataAccess;

    public DataAccess getDataAccess() {
        if (mDataAccess == null) {
            mDataAccess = new DataAccess();
        }
        return mDataAccess;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

}
