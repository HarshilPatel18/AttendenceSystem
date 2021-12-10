package com.cyberia.attendencesystem.View.Network;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    public static final String SHARED_PREF_NAME="com.msu.android.attendence.settings";
    public static final String KEY_ACCESS_TOKEN="access_token";
    public static final String TOKEN_EXPIRY_TIME="expiry";


    public SharedPrefManager(Context context) {
        mCtx = context;

    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public static boolean userLogin(String token,int exp)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        long expiry=System.currentTimeMillis()+(exp*1000);

        editor.putString(KEY_ACCESS_TOKEN,token).apply();
        editor.putLong(TOKEN_EXPIRY_TIME,expiry);
        editor.apply();
        return true;

    }

    public static boolean isLoggedIn()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        long exp_time=sharedPreferences.getLong(TOKEN_EXPIRY_TIME,0);
        if(sharedPreferences.getString(KEY_ACCESS_TOKEN,null) !=null)
        {
            return true;
        }
        else if(System.currentTimeMillis()<=exp_time)
        {
            return true;
        }
        else {
            logout();
            return false;
        }

    }

    public static boolean logout()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_ACCESS_TOKEN,null);
        editor.clear();
        editor.apply();
        return true;
    }

    public static String getAccessToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN,null);
    }
}
