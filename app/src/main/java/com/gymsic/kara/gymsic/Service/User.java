package com.gymsic.kara.gymsic.Service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gymsic.kara.gymsic.Model.UserModel;

public class User {


    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private final String KEY_FB_ID = "KEY_FB_ID";
    private final String KEY_FB_NAME = "KEY_FB_NAME";
    private final String KEY_FB_EMAIL = "KEY_FB_EMAIL";

    private final String KEY_PREFS = "prefs_user";

    private Context context;

    public User(Context context){
        this.context = context;
        mPrefs = this.context.getSharedPreferences(KEY_PREFS, context.MODE_PRIVATE);
        mEditor = mPrefs.edit();
    }

    public boolean saveUserInfo(UserModel userModle){
        mEditor.putString(KEY_FB_ID, userModle.getFbID());
        mEditor.putString(KEY_FB_NAME, userModle.getName());
        mEditor.putString(KEY_FB_EMAIL, userModle.getEmail());
        return mEditor.commit();
    }

    public boolean isLogin(){
        String fbID = mPrefs.getString(KEY_FB_ID, "");
        String name = mPrefs.getString(KEY_FB_NAME, "");
        String email = mPrefs.getString(KEY_FB_EMAIL, "");
        if ( !name.isEmpty() ) {
            return true;
        }
        return false;
    }

    public boolean logout(){
        mEditor.putString(KEY_FB_ID, "");
        mEditor.putString(KEY_FB_NAME, "");
        mEditor.putString(KEY_FB_EMAIL, "");
        return mEditor.commit();
    }
}
