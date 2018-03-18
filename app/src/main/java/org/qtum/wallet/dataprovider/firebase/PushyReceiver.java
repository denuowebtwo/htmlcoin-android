package org.qtum.wallet.dataprovider.firebase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.qtum.wallet.R;
import org.qtum.wallet.datastorage.QtumSharedPreference;
import org.qtum.wallet.ui.activity.main_activity.MainActivity;
import org.qtum.wallet.utils.QtumIntent;

public class PushyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationTitle = "HTMLCOIN";
        String notificationText = "New Transaction";

        // Attempt to extract the "message" property from the payload: {"message":"Hello World!"}
        if (intent.getStringExtra("body") != null) {
            notificationText = intent.getStringExtra("body");
        }

        sendNotification(context, "", notificationTitle, notificationText);

        QtumSharedPreference.getInstance().setIsRefreshNeeded(context, true);

        Intent refreshIntent = new Intent();
        refreshIntent.setAction(QtumIntent.NEW_TRANSACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);
    }

    private void sendNotification(Context context, String Ticker, String Title, String Text) {

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(QtumIntent.OPEN_FROM_NOTIFICATION);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        builder.setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setTicker(Ticker)
                .setContentTitle(Title)
                .setContentText(Text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());


        Notification notification = builder.build();
        notificationManager.notify(108, notification);
    }
}
