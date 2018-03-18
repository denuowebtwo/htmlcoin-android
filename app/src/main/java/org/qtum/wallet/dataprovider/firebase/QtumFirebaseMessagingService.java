package org.qtum.wallet.dataprovider.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.qtum.wallet.R;
import org.qtum.wallet.datastorage.QtumSharedPreference;
import org.qtum.wallet.ui.activity.main_activity.MainActivity;
import org.qtum.wallet.utils.QtumIntent;

public class QtumFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        QtumSharedPreference.getInstance().setIsRefreshNeeded(getApplicationContext(), true);

        Intent intent = new Intent();
        intent.setAction(QtumIntent.NEW_TRANSACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

//        if(QtumApplication.instance == null) {
            sendNotification("", "HTMLCOIN", remoteMessage.getNotification().getBody());
//        }



    }

    private void sendNotification(String Ticker, String Title, String Text) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(QtumIntent.OPEN_FROM_NOTIFICATION);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        builder.setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setTicker(Ticker)
                .setContentTitle(Title)
                .setContentText(Text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis());

        Notification notification = builder.build();
        notificationManager.notify(108, notification);
    }
}
