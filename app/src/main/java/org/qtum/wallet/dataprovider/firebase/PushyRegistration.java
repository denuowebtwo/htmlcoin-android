package org.qtum.wallet.dataprovider.firebase;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.KeyStorage;
import org.qtum.wallet.model.gson.AddressDeviceTokenRequest;
import org.qtum.wallet.model.gson.AddressDeviceTokenResponse;
import org.qtum.wallet.utils.LogUtils;

import java.util.List;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class PushyRegistration {
    private static final String LOG_TAG = "PushyRegistration";

    public static Boolean isGoogleApiAvailable(Context context) {
        // detect play services
        int googleApiAvailabilityResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (    googleApiAvailabilityResult == ConnectionResult.SERVICE_MISSING ||
                googleApiAvailabilityResult == ConnectionResult.SERVICE_INVALID ||
                googleApiAvailabilityResult == ConnectionResult.SERVICE_DISABLED) {

            return false;
        }
        return true;
    }

    public static void listenPushyService(Activity activity, final Context context) {
        int googleApAvailabilityResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if ( !isGoogleApiAvailable(context)) {
            // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
                // Pushy SDK will be able to persist the device token in the external storage
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                String pushToken = TokenSharedPreferences.getInstance().getPushyToken(context);
                if (pushToken == null) {
                    Observable.create(new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> subscriber) {
                            try {
                                String deviceToken = "";

                                if (!Pushy.isRegistered(context)) {
                                    deviceToken = Pushy.register(context);
                                } else {
                                    deviceToken = Pushy.getDeviceCredentials(context).token;
                                }

                                Pushy.listen(context);

                                TokenSharedPreferences.getInstance().savePushyToken(context, deviceToken);
                                updatePushyDeviceToken(context);
                            } catch (PushyException e) {
                                LogUtils.error("Pushy", e.getMessage(), e);
                            } finally {
                                subscriber.onCompleted();
                            }
                        }
                    }).subscribeOn(Schedulers.io()).subscribe();
                } else {
                    Pushy.listen(context);
                }
            }
        }
    }

    public static void checkPushServices(Activity activity, Context context) {
        int googleApAvailabilityResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if ( !isGoogleApiAvailable(context)) {
            updatePushyDeviceToken(context);
        } else {
            Pushy.toggleNotifications(false, context);
            TokenSharedPreferences.getInstance().deletePushyToken(context);
            deletePushyDeviceToken(context);
        }
    }

    /**
     * Update pushy token to server
     * @param context
     */
    public static void updatePushyDeviceToken(Context context) {
        List<String> addresses = KeyStorage.getInstance().getAddresses();
        String token = TokenSharedPreferences.getInstance().getPushyToken(context);

        if (addresses == null || addresses.size() == 0 || token == null || token.isEmpty()) return;

        AddressDeviceTokenRequest addressDeviceToken = new AddressDeviceTokenRequest(addresses.toArray(new String[0]), null, token);

        QtumService.newInstance().updatePushyDeviceToken(addressDeviceToken)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AddressDeviceTokenResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.error(LOG_TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(AddressDeviceTokenResponse addressDeviceTokenResponse) {
                        LogUtils.info(LOG_TAG, "Update pushy token success");
                    }
                });

    }

    /**
     * Delete pushy token in server
     * @param context
     */
    public static void deletePushyDeviceToken(Context context) {
        List<String> addresses = KeyStorage.getInstance().getAddresses();

        if (addresses == null || addresses.size() == 0) return;


        AddressDeviceTokenRequest addressDeviceToken = new AddressDeviceTokenRequest(addresses.toArray(new String[0]), null, null);

        QtumService.newInstance().deletePushyDeviceToken(addressDeviceToken)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AddressDeviceTokenResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.error(LOG_TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(AddressDeviceTokenResponse addressDeviceTokenResponse) {
                        LogUtils.info(LOG_TAG, "Delete pushy token success");
                    }
                });

    }
}
