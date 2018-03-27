package org.qtum.wallet.ui.fragment.wallet_fragment;

import android.widget.Toast;

import org.qtum.wallet.datastorage.HistoryList;
import org.qtum.wallet.datastorage.realm.RealmStorage;
import org.qtum.wallet.model.AddressWithBalance;
import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.model.gson.history.HistoryResponse;
import org.qtum.wallet.model.gson.history.TransactionReceipt;
import org.qtum.wallet.model.gson.history.Vin;
import org.qtum.wallet.model.gson.history.Vout;
import org.qtum.wallet.ui.base.AddressInteractor;
import org.qtum.wallet.ui.base.AddressInteractorImpl;
import org.qtum.wallet.ui.base.base_fragment.BaseFragment;
import org.qtum.wallet.ui.base.base_fragment.BaseFragmentPresenterImpl;

import java.math.BigDecimal;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;

public class WalletPresenterImpl extends BaseFragmentPresenterImpl implements WalletPresenter {
    private WalletInteractor mWalletFragmentInteractor;
    private WalletView mWalletView;
    private boolean mVisibility = false;
    private Boolean mNetworkConnectedFlag = null;
    private SubscriptionList mSubscriptionList = new SubscriptionList();

    private final int ONE_PAGE_COUNT = 25;

    private AddressInteractor mAddressInteractor;

    public WalletPresenterImpl(WalletView walletView, WalletInteractor interactor) {
        mWalletView = walletView;
        mWalletFragmentInteractor = interactor;
        mAddressInteractor = new AddressInteractorImpl(walletView.getContext());
    }


    @Override
    public void notifyHeader() {
        String pubKey = getInteractor().getAddress();
        if (pubKey != null && !pubKey.isEmpty())
            getView().updatePubKey(pubKey);

        AddressBalance addressBalance = RealmStorage.getInstance(mWalletView.getContext()).getAddressBalance();
        if (addressBalance != null){
            getView().updateBalance(addressBalance.getFormattedBalance().stripTrailingZeros().toPlainString(), addressBalance.getFormattedUnconfirmedBalance().stripTrailingZeros().toPlainString());
        } else {
            loadAndUpdateData();
        }
    }

    @Override
    public WalletView getView() {
        return mWalletView;
    }

    private WalletInteractor getInteractor() {
        return mWalletFragmentInteractor;
    }

    @Override
    public void onRefresh() {
        if (mNetworkConnectedFlag) {
            loadAndUpdateData();
        } else {
            getView().setAlertDialog(org.qtum.wallet.R.string.no_internet_connection,
                    org.qtum.wallet.R.string.please_check_your_network_settings,
                    org.qtum.wallet.R.string.ok,
                    BaseFragment.PopUpType.error);
            getView().stopRefreshRecyclerAnimation();
        }
    }

    @Override
    public void openTransactionFragment(int position) {
        getView().openTransactionsFragment(position);
    }

    @Override
    public void onLastItem(final int currentItemCount) {
        if (getInteractor().getHistoryList().size() != getInteractor().getTotalHistoryItem()) {
            getView().loadNewHistory();
            mSubscriptionList.add(getInteractor().getHistoryList(ONE_PAGE_COUNT, currentItemCount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HistoryResponse>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(HistoryResponse historyResponse) {
                            for (History history : historyResponse.getItems()) {
                                calculateChangeInBalance(history, getInteractor().getAddresses());
                            }
                            HistoryList.getInstance(getView().getContext()).getHistoryList().addAll(historyResponse.getItems());
                            getView().addHistory(currentItemCount, getInteractor().getHistoryList().size() - currentItemCount + 1,
                                    getInteractor().getHistoryList());
                            initTransactionReceipt(historyResponse.getItems());
                        }
                    }));

        }
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
                if (address.equals(vin.getAddress())) {
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


    private void loadAndUpdateData() {
        getView().startRefreshAnimation();
        mSubscriptionList.add(getInteractor().getHistoryList(ONE_PAGE_COUNT, 0).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HistoryResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().stopRefreshRecyclerAnimation();

                        if (!getNetworkConnectedFlag()) {
                            getView().setAlertDialog(org.qtum.wallet.R.string.no_internet_connection,
                                    org.qtum.wallet.R.string.please_check_your_network_settings,
                                    org.qtum.wallet.R.string.ok,
                                    BaseFragment.PopUpType.error);
                        } else {
                            Toast.makeText(getView().getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(final HistoryResponse historyResponse) {
                        for (History history : historyResponse.getItems()) {
                            calculateChangeInBalance(history, getInteractor().getAddresses());
                        }
                        HistoryList.getInstance(getView().getContext()).setHistoryList(historyResponse.getItems());
                        HistoryList.getInstance(getView().getContext()).setTotalItem(historyResponse.getTotalItems());
                        initTransactionReceipt(historyResponse.getItems());
                        getView().updateHistory(getInteractor().getHistoryList());
                    }
                }));

        loadAndUpdateBalance();
    }

    @Override
    public List<History> getHistoryList() {
        return getInteractor().getHistoryList();
    }

    private void initTransactionReceipt(final List<History> histories) {
        for (final History history : histories) {
            if (history.getTransactionReceipt() == null) {
                getInteractor().getTransactionReceipt(history.getTxHash())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<TransactionReceipt>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(final List<TransactionReceipt> transactionReceipt) {
                                if(transactionReceipt.size()>0) {
                                    history.setTransactionReceipt(transactionReceipt.get(0));

                                } else {
                                    history.setReceiptUpdated(true);
                                }
                                getView().notifyConfirmHistory(histories.indexOf(history));
                            }
                        });
            }
        }
    }

    @Override
    public void onNetworkStateChanged(boolean networkConnectedFlag) {
        if (networkConnectedFlag && mNetworkConnectedFlag != null && !mNetworkConnectedFlag) {
            loadAndUpdateData();
        }
        mNetworkConnectedFlag = networkConnectedFlag;
    }

    public Boolean getNetworkConnectedFlag() {
        return mNetworkConnectedFlag;
    }

    @Override
    public void onNewHistory(History history) {
        if (history.getBlockTime() != null) {
            calculateChangeInBalance(history, getInteractor().getAddresses());
            Integer notifyPosition = getInteractor().setHistory(history);
            if (notifyPosition == null) {
                getView().notifyNewHistory();
            } else {
                getView().notifyConfirmHistory(notifyPosition);
            }
        } else {
            calculateChangeInBalance(history, getInteractor().getAddresses());
            getInteractor().addToHistoryList(history);
            getView().notifyNewHistory();
        }
    }

    @Override
    public boolean getVisibility() {
        return mVisibility;
    }

    @Override
    public void updateVisibility(boolean value) {
        this.mVisibility = value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscriptionList != null) {
            mSubscriptionList.clear();
        }
    }

    private void loadAndUpdateBalance() {
        final List<String> addresses = mAddressInteractor.getAddresses();
        mAddressInteractor.getAddressBalance(addresses)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AddressBalance>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AddressBalance addressBalance) {
                        getView().updateBalance(addressBalance.getFormattedBalance().stripTrailingZeros().toPlainString(), addressBalance.getFormattedUnconfirmedBalance().stripTrailingZeros().toPlainString());
                    }
                });
    }

    /***
     * Setter for unit tests
     */
    public void setNetworkConnectedFlag(boolean mNetworkConnectedFlag) {
        this.mNetworkConnectedFlag = mNetworkConnectedFlag;
    }
}