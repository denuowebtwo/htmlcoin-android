package org.qtum.wallet.dataprovider.firebase;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.KeyStorage;
import org.qtum.wallet.model.gson.AddressDeviceTokenRequest;
import org.qtum.wallet.model.gson.AddressDeviceTokenResponse;

import java.util.List;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.util.exceptions.PushyException;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class PushyRegistration {
    private static final String LOG_TAG = "PushyRegistration";

    public static void registerPushyDeviceToken(final Context context) {
        // detect play services
        int googleApAvailabilityResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (    googleApAvailabilityResult == ConnectionResult.SERVICE_MISSING ||
                googleApAvailabilityResult == ConnectionResult.SERVICE_INVALID ||
                googleApAvailabilityResult == ConnectionResult.SERVICE_DISABLED) {

            String pushToken = TokenSharedPreferences.getInstance().getPushyToken(context);
            if (pushToken == null) {
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            String deviceToken = Pushy.register(context);

                            TokenSharedPreferences.getInstance().savePushyToken(context, deviceToken);
                            updatePushyDeviceToken(context);

                            subscriber.onNext(deviceToken);
                        } catch (PushyException e) {
                            Log.e("Pushy", e.getMessage(), e);
                            subscriber.onNext("");
                        } finally {

                            subscriber.onCompleted();
                        }
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .subscribe();


            }
        }
    }

    public static void checkPushServices(Context context) {
        int googleApAvailabilityResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (    googleApAvailabilityResult == ConnectionResult.SERVICE_MISSING ||
                googleApAvailabilityResult == ConnectionResult.SERVICE_INVALID ||
                googleApAvailabilityResult == ConnectionResult.SERVICE_DISABLED) {

            updatePushyDeviceToken(context);
        } else {
            deletePushyDeviceToken(context);

            TokenSharedPreferences.getInstance().deletePushyToken(context);
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
        int googleApAvailabilityResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (!(    googleApAvailabilityResult == ConnectionResult.SERVICE_MISSING ||
                googleApAvailabilityResult == ConnectionResult.SERVICE_INVALID ||
                googleApAvailabilityResult == ConnectionResult.SERVICE_DISABLED)) return;


        AddressDeviceTokenRequest addressDeviceToken = new AddressDeviceTokenRequest(addresses.toArray(new String[0]), null, token);

        QtumService.newInstance().updatePushyDeviceToken(addressDeviceToken)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AddressDeviceTokenResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(AddressDeviceTokenResponse addressDeviceTokenResponse) {
                        Log.i(LOG_TAG, "Update pushy token success");
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
                        Log.e(LOG_TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(AddressDeviceTokenResponse addressDeviceTokenResponse) {
                        Log.i(LOG_TAG, "Delete pushy token success");
                    }
                });

    }
}
