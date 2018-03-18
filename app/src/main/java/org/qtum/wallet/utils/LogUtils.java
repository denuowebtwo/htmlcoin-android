package org.qtum.wallet.utils;

import android.util.Log;

import org.qtum.wallet.BuildConfig;

public class LogUtils {
    public static void info(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void debug(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void warn(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void error(final String tag, String message) {
        Log.e(tag, message);
    }

    public static void error(final String tag, String message, Throwable e) {
        Log.e(tag, message, e);
    }
}
