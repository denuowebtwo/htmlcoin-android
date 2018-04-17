package org.qtum.wallet.ui.base;

import android.content.Context;
import android.util.Log;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.KeyStorage;
import org.qtum.wallet.datastorage.realm.RealmStorage;
import org.qtum.wallet.model.AddressWithBalance;
import org.qtum.wallet.model.contract.Token;
import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.AddressDeviceTokenRequest;
import org.qtum.wallet.model.gson.AddressDeviceTokenResponse;
import org.qtum.wallet.model.gson.UnspentOutput;
import org.qtum.wallet.model.gson.token_balance.TokenBalanceResponse;
import org.qtum.wallet.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddressInteractorImpl implements AddressInteractor {
    private static final String LOG_TAG = "AddressInteractor";
    private WeakReference<Context> mContext;

    public AddressInteractorImpl(Context context) {
        mContext = new WeakReference<Context>(context);
    }

    @Override
    public List<String> getAddresses() {
        return KeyStorage.getInstance().getAddresses();
    }

    @Override
    public Observable<List<UnspentOutput>> getUnspentOutputs(List<String> addresses){
        return QtumService.newInstance().getUnspentOutputsForSeveralAddresses(addresses);
    }

    @Override
    public Observable<List<AddressWithBalance>> getAddressBalances(final List<String> addresses) {
        return Observable.create(new Observable.OnSubscribe<List<AddressWithBalance>>(){
            @Override
            public void call(final Subscriber<? super List<AddressWithBalance>> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        QtumService.newInstance().getUnspentOutputsForSeveralAddresses(addresses)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<List<UnspentOutput>>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                    }

                                    @Override
                                    public void onNext(List<UnspentOutput> unspentOutputs) {
                                        List<AddressWithBalance> mAddressWithBalanceList = new ArrayList<>();

                                        for(UnspentOutput unspentOutput : unspentOutputs) {
                                            for (String address : addresses) {
                                                if(unspentOutput.getAddress().equals(address)){
                                                    AddressWithBalance addressWithBalance = new AddressWithBalance(address);
                                                    addressWithBalance.setUnspentOutput(unspentOutput);
                                                    mAddressWithBalanceList.add(addressWithBalance);
                                                    break;
                                                }
                                            }
                                        }

                                        subscriber.onNext(mAddressWithBalanceList);
                                        subscriber.onCompleted();
                                    }
                                });
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<AddressBalance> getAddressBalance(final List<String> addresses) {
//        return QtumService.newInstance().getAddressBalance(addresses);

        return Observable.create(new Observable.OnSubscribe<AddressBalance>(){
            @Override
            public void call(final Subscriber<? super AddressBalance> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        QtumService.newInstance().getAddressBalance(addresses)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<AddressBalance>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(AddressBalance addressBalance) {
                                        RealmStorage realmStorage = RealmStorage.getInstance(mContext.get());
                                        realmStorage.updateAddressBalance(addressBalance);

                                        subscriber.onNext(addressBalance);
                                        subscriber.onCompleted();
                                    }
                                });
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public Observable<TokenBalanceResponse> getTokenBalance(final Token token, final List<String> addresses) {
        return QtumService.newInstance().getTokenBalances(token.getContractAddress(), 20, 0, addresses);
    }

    @Override
    public Boolean updateAddressDeviceToken(String[] addresses, String token) {
        AddressDeviceTokenRequest addressDeviceToken = new AddressDeviceTokenRequest(addresses, token, null);

        QtumService.newInstance().updateDeviceToken(addressDeviceToken)
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
                        LogUtils.info(LOG_TAG, "Update firebase token success");
                    }
                });

        return true;
    }

    @Override
    public Boolean updatePushyDeviceToken(String[] addresses, String token) {
        AddressDeviceTokenRequest addressDeviceToken = new AddressDeviceTokenRequest(addresses, null, token);

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

        return true;
    }
}
