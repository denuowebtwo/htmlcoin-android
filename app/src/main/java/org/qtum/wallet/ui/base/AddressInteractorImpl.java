package org.qtum.wallet.ui.base;

import android.content.Context;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.KeyStorage;
import org.qtum.wallet.model.AddressWithBalance;
import org.qtum.wallet.model.gson.UnspentOutput;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class AddressInteractorImpl implements AddressInteractor {

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
    public Observable<BigDecimal> getAddressBalance(final List<String> addresses) {
        return Observable.create(new Observable.OnSubscribe<BigDecimal>() {
            @Override
            public void call(final Subscriber<? super BigDecimal> subscriber) {
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
                                        BigDecimal balance = BigDecimal.ZERO;

                                        for(UnspentOutput unspentOutput : unspentOutputs) {
                                            for (String address : addresses) {
                                                if(unspentOutput.getAddress().equals(address)){
                                                    balance = balance.add(unspentOutput.getAmount());
                                                    break;
                                                }
                                            }
                                        }

                                        subscriber.onNext(balance);
                                        subscriber.onCompleted();
                                    }
                                });
                    }
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
