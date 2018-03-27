package org.qtum.wallet.ui.fragment.wallet_fragment;


import android.content.Context;
import android.text.TextUtils;

import org.qtum.wallet.dataprovider.rest_api.QtumService;
import org.qtum.wallet.datastorage.QtumSharedPreference;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.model.gson.history.HistoryResponse;
import org.qtum.wallet.model.gson.history.TransactionReceipt;
import org.qtum.wallet.model.gson.history.Vin;
import org.qtum.wallet.model.gson.history.Vout;
import org.qtum.wallet.datastorage.HistoryList;
import org.qtum.wallet.datastorage.KeyStorage;

import java.math.BigDecimal;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;

public class WalletInteractorImpl implements WalletInteractor {

    private SubscriptionList mSubscriptionList = new SubscriptionList();
    static final int UPDATE_STATE = 0;
    static final int LOAD_STATE = 1;
    private final List<String> addresses = KeyStorage.getInstance().getAddresses();
    private Context mContext;

    public WalletInteractorImpl(Context context) {
        mContext = context;
    }

    @Override
    public List<History> getHistoryList() {
        return HistoryList.getInstance(mContext).getHistoryList();
    }


    @Override
    public Observable<HistoryResponse> getHistoryList(int limit, int offest) {
        return QtumService.newInstance().getHistoryListForSeveralAddresses(getAddresses(), limit, offest);
    }

    @Override
    public List<String> getAddresses() {
        return KeyStorage.getInstance().getAddresses();
    }

    private void calculateChangeInBalance(History history, List<String> addresses) {
        BigDecimal changeInBalance = calculateVout(history, addresses).subtract(calculateVin(history, addresses));
        history.setChangeInBalance(changeInBalance);
    }

    private BigDecimal calculateVin(History history, List<String> addresses) {
        BigDecimal totalVin = new BigDecimal("0.0");
        boolean equals = false;
        for (Vin vin : history.getVin()) {
            for (String address : addresses) {
                if (vin.getAddress().equals(address)) {
                    vin.setOwnAddress(true);
                    equals = true;
                }
            }
        }
        if (equals) {
            totalVin = history.getAmount();
        }
        return totalVin;
    }

    private BigDecimal calculateVout(History history, List<String> addresses) {
        BigDecimal totalVout = new BigDecimal("0.0");
        for (Vout vout : history.getVout()) {
            for (String address : addresses) {
                if (address.equals(vout.getAddress())) {
                    vout.setOwnAddress(true);
                    totalVout = totalVout.add(vout.getValue());
                }
            }
        }
        return totalVout;
    }

    @Override
    public int getTotalHistoryItem() {
        return HistoryList.getInstance(mContext).getTotalItem();
    }

    @Override
    public void addToHistoryList(History history) {
        HistoryList.getInstance(mContext).getHistoryList().add(0, history);
    }

    @Override
    public Integer setHistory(History history) {
        for (History historyReplacing : getHistoryList()) {
            if (historyReplacing.getTxHash().equals(history.getTxHash())) {
                int position = getHistoryList().indexOf(historyReplacing);
                getHistoryList().set(position, history);
                return position;
            }
        }
        getHistoryList().add(0, history);
        return null;
    }

    @Override
    public String getAddress() {
        String s = KeyStorage.getInstance().getCurrentAddress();
        if(!TextUtils.isEmpty(s)) {
            QtumSharedPreference.getInstance().saveCurrentAddress(mContext, s);
        }
        return s;
    }

    @Override
    public Observable<List<TransactionReceipt>> getTransactionReceipt(String txHash) {
        return QtumService.newInstance().getTransactionReceipt(txHash);
    }
}