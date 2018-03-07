package org.qtum.wallet.ui.fragment.language_fragment;


import android.content.Context;
import android.util.Pair;

import org.qtum.wallet.R;
import org.qtum.wallet.datastorage.QtumSharedPreference;
import org.qtum.wallet.datastorage.listeners.LanguageChangeListener;

import java.util.ArrayList;
import java.util.List;

class LanguageInteractorImpl implements LanguageInteractor{

    private Context mContext;
    private List<Pair<String,String>> mLanguagesList;

    LanguageInteractorImpl(Context context){
        mContext = context;
        mLanguagesList = new ArrayList<>();
        mLanguagesList.add(new Pair<>("zh", context.getString(R.string.arabic)));
        mLanguagesList.add(new Pair<>("zh", context.getString(R.string.chinese)));
        mLanguagesList.add(new Pair<>("da", context.getString(R.string.danish)));
        mLanguagesList.add(new Pair<>("us", context.getString(R.string.english)));
        mLanguagesList.add(new Pair<>("fr", context.getString(R.string.french)));
        mLanguagesList.add(new Pair<>("de", context.getString(R.string.german)));
//        mLanguagesList.add(new Pair<>("ja", context.getString(R.string.japanese)));
        mLanguagesList.add(new Pair<>("ko", context.getString(R.string.korean)));
        mLanguagesList.add(new Pair<>("pt", context.getString(R.string.portuguese)));
        mLanguagesList.add(new Pair<>("es", context.getString(R.string.spanish)));
    }

    @Override
    public String getLanguage(){
        return  QtumSharedPreference.getInstance().getLanguage(mContext);
    }

    @Override
    public void setLanguage(String language){
        QtumSharedPreference.getInstance().saveLanguage(mContext, language);
    }

    @Override
    public List<Pair<String,String>> getLanguagesList(){
        return mLanguagesList;
    }

    @Override
    public void removeLanguageListener(LanguageChangeListener languageChangeListener) {
        QtumSharedPreference.getInstance().removeLanguageListener(languageChangeListener);
    }

    @Override
    public void addLanguageListener(LanguageChangeListener languageChangeListener) {
        QtumSharedPreference.getInstance().addLanguageListener(languageChangeListener);
    }

}