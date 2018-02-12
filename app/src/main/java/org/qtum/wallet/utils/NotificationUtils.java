package org.qtum.wallet.utils;


import android.app.NotificationManager;
import android.content.Context;

public class NotificationUtils {
    public static void cancelNotification(Context mContext, int notiId) {
        if (mContext != null) {
            NotificationManager mNotifyMgr =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.cancel(notiId);
        }
    }

    public static void cancelAllNotifications(Context mContext) {
        if (mContext != null) {
            NotificationManager mNotifyMgr =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.cancelAll();
        }
    }
}
