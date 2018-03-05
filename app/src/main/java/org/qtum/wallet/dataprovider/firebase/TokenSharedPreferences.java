package org.qtum.wallet.dataprovider.firebase;

import android.content.Context;
import android.content.SharedPreferences;

import org.qtum.wallet.dataprovider.firebase.listeners.FireBaseTokenRefreshListener;

public class TokenSharedPreferences {

    private final String FIREBASE_DATA_STORAGE = "firebase_data_storage";
    private static TokenSharedPreferences sInstance;

    private final String PREV_TOKEN = "prev_token";
    private final String CURRENT_TOKEN = "current_token";
    private final String PUSHY_TOKEN = "pushy_token";

    private FireBaseTokenRefreshListener mFireBaseTokenRefreshListener;

    private TokenSharedPreferences(){
    }

    public static TokenSharedPreferences getInstance() {
        if (sInstance == null) {
            sInstance = new TokenSharedPreferences();
        }
        return sInstance;
    }

    public void saveFirebaseToken(Context context, String token){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(FIREBASE_DATA_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        String prevToken = mSharedPreferences.getString(CURRENT_TOKEN,null);
        mEditor.putString(PREV_TOKEN,prevToken);
        mEditor.putString(CURRENT_TOKEN, token);
        mEditor.apply();
        if(mFireBaseTokenRefreshListener != null) {
            mFireBaseTokenRefreshListener.onRefresh(prevToken, token);
        }
    }

    public String[] getFirebaseTokens(Context context){
        String[] firebaseTokens = new String[2];
        SharedPreferences mSharedPreferences = context.getSharedPreferences(FIREBASE_DATA_STORAGE, Context.MODE_PRIVATE);
        firebaseTokens[0] = mSharedPreferences.getString(PREV_TOKEN,null);
        firebaseTokens[1] = mSharedPreferences.getString(CURRENT_TOKEN,null);
        return firebaseTokens;
    }

    public void addFirebaseTokenRefreshListener(FireBaseTokenRefreshListener fireBaseTokenRefreshListener){
        mFireBaseTokenRefreshListener = fireBaseTokenRefreshListener;
    }

    public void removeFirebaseTokenRefreshListener(){
        mFireBaseTokenRefreshListener = null;
    }

    public void savePushyToken(Context context, String token){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(FIREBASE_DATA_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(PUSHY_TOKEN, token);
        mEditor.apply();
    }

    public String getPushyToken(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(FIREBASE_DATA_STORAGE, Context.MODE_PRIVATE);
        return mSharedPreferences.getString(PUSHY_TOKEN,null);
    }

    public void deletePushyToken(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(FIREBASE_DATA_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.remove(PUSHY_TOKEN);
        mEditor.apply();
    }
}
