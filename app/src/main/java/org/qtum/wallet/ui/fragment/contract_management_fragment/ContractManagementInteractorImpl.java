package org.qtum.wallet.ui.fragment.contract_management_fragment;

import android.content.Context;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.ContractStorage;
import org.qtum.wallet.datastorage.FileStorageManager;
import org.qtum.wallet.datastorage.TinyDB;
import org.qtum.wallet.model.AddressWithBalance;
import org.qtum.wallet.model.contract.Contract;
import org.qtum.wallet.model.contract.ContractMethod;
import org.qtum.wallet.model.gson.SmartContractInfo;

import java.lang.ref.WeakReference;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


public class ContractManagementInteractorImpl implements ContractManagementInteractor{

    WeakReference<Context> mContext;

    ContractManagementInteractorImpl(Context context){
        mContext = new WeakReference<Context>(context);
    }

    @Override
    public List<ContractMethod> getContractListByAbi(String abi) {
        return FileStorageManager.getInstance().getContractMethodsByAbiString(mContext.get(), abi);
    }

    @Override
    public Contract getContractByAddress(String address) {
        TinyDB tinyDB = new TinyDB(mContext.get());
        for(Contract contract : tinyDB.getContractList()){
            if(contract.getContractAddress().equals(address)){
                return contract;
            }
        }
        return null;
    }

    @Override
    public List<ContractMethod> getContractListByUiid(String uiid) {
        return  FileStorageManager.getInstance().getContractMethods(mContext.get(), uiid);
    }

    public Observable<SmartContractInfo> getContractInfo(final String address) {
        return Observable.create(new Observable.OnSubscribe<SmartContractInfo>() {
            @Override
            public void call(final Subscriber<? super SmartContractInfo> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        QtumService.newInstance().getContractInfo(address)
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<SmartContractInfo>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(SmartContractInfo smartContractInfo) {
                                        ContractStorage contractStorage = ContractStorage.getInstance(mContext.get());
                                        contractStorage.updateContractInfo(smartContractInfo);

                                        subscriber.onNext(smartContractInfo);
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
}
