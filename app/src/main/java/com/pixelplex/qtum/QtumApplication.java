package com.pixelplex.qtum;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.pixelplex.qtum.utils.FontManager;

import io.fabric.sdk.android.Fabric;

public class QtumApplication extends MultiDexApplication {

    private int contractAwaitCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        FontManager.init(getAssets());
    }

    public int getContractAwaitCount() {
        return contractAwaitCount;
    }

    public void setContractAwaitCount(int contractAwaitCount) {
        this.contractAwaitCount = contractAwaitCount;
    }

    public void setContractAwaitCountPlus() {
        this.contractAwaitCount++;
    }

    public void setContractAwaitCountMinus() {
        this.contractAwaitCount--;
    }
}