package org.qtum.wallet.ui.fragment.my_contracts_fragment;

import android.content.Context;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.ContractStorage;
import org.qtum.wallet.datastorage.QtumSettingSharedPreference;
import org.qtum.wallet.datastorage.QtumSharedPreference;
import org.qtum.wallet.datastorage.TinyDB;
import org.qtum.wallet.model.contract.Contract;
import org.qtum.wallet.model.contract.Token;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.utils.ContractBuilder;
import org.qtum.wallet.utils.DateCalculator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;


public class MyContractsInteractorImpl implements MyContractsInteractor {
    private WeakReference<Context> mContext;

    public MyContractsInteractorImpl(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    public List<Contract> getContracts() {
        TinyDB tinyDB = new TinyDB(mContext.get());
        return tinyDB.getContractList();
    }

    @Override
    public List<Contract> getContractsWithoutTokens() {
        TinyDB tinyDB = new TinyDB(mContext.get());
        return tinyDB.getContractListWithoutToken();
    }

    @Override
    public List<Token> getTokens() {
        TinyDB tinyDB = new TinyDB(mContext.get());
        return tinyDB.getTokenList();
    }

    @Override
    public void setContractWithoutTokens(List<Contract> contracts) {
        TinyDB tinyDB = new TinyDB(mContext.get());
        tinyDB.putContractListWithoutToken(contracts);
    }

    @Override
    public void setTokens(List<Token> tokens) {
        TinyDB tinyDB = new TinyDB(mContext.get());
        tinyDB.putTokenList(tokens);
    }

    @Override
    public boolean isShowWizard() {
        QtumSettingSharedPreference qtumSettingSharedPreference = new QtumSettingSharedPreference();
        return qtumSettingSharedPreference.getShowContractsDeleteUnsubscribeWizard(mContext.get());
    }

    @Override
    public void setShowWizard(boolean isShow) {
        QtumSettingSharedPreference qtumSettingSharedPreference = new QtumSettingSharedPreference();
        qtumSettingSharedPreference.setShowContractsDeleteUnsubscribeWizard(mContext.get(), isShow);
    }

    public void checkConfirmContract() {
        final TinyDB tinyDB = new TinyDB(mContext.get());
        final ArrayList<String> unconfirmedContractTxHashList = tinyDB.getUnconfirmedContractTxHasList();
        for(final String unconfirmedContractTxHash : unconfirmedContractTxHashList){
            QtumService.newInstance()
                    .getTransaction(unconfirmedContractTxHash)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<History>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(History history) {
                            ContractStorage contractStorage = ContractStorage.getInstance(mContext.get());
                            if (history.isContractTransaction()) {
                                contractStorage.updateContractStatus(history);
                            }
                        }
                    });
        }
    }
}
