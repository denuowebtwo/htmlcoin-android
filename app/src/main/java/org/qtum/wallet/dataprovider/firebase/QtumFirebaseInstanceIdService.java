package org.qtum.wallet.dataprovider.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.KeyStorage;
import org.qtum.wallet.model.gson.AddressDeviceTokenRequest;
import org.qtum.wallet.model.gson.AddressDeviceTokenResponse;
import org.qtum.wallet.utils.LogUtils;

import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;


public class QtumFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        LogUtils.debug(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        TokenSharedPreferences.getInstance().saveFirebaseToken(getApplicationContext(),token);

        // send new token to server
        updateAddressDeviceToken();
    }

    private void updateAddressDeviceToken() {
        List<String> addresses = KeyStorage.getInstance().getAddresses();
        String[] firebaseTokens = TokenSharedPreferences.getInstance().getFirebaseTokens(getApplicationContext());
        String token = firebaseTokens[1];

        if (addresses == null || addresses.size() == 0 || token == null || token.isEmpty()) return;

        AddressDeviceTokenRequest addressDeviceToken = new AddressDeviceTokenRequest(addresses.toArray(new String[0]), token, null);

        QtumService.newInstance().updateDeviceToken(addressDeviceToken)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AddressDeviceTokenResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.error(TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(AddressDeviceTokenResponse addressDeviceTokenResponse) {
                        LogUtils.info(TAG, "Success");
                    }
                });

    }
}