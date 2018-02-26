package org.qtum.wallet.ui.fragment.wallet_fragment;

import org.qtum.wallet.datastorage.realm.RealmStorage;
import org.qtum.wallet.model.AddressWithBalance;
import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.ui.base.AddressInteractor;
import org.qtum.wallet.ui.base.AddressInteractorImpl;
import org.qtum.wallet.ui.base.base_fragment.BaseFragment;
import org.qtum.wallet.ui.base.base_fragment.BaseFragmentPresenterImpl;

import java.math.BigDecimal;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WalletPresenterImpl extends BaseFragmentPresenterImpl implements WalletPresenter {
    private WalletInteractor mWalletFragmentInteractor;
    private WalletView mWalletView;
    private boolean mVisibility = false;
    private Boolean mNetworkConnectedFlag = null;

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
            getInteractor().getHistoryList(WalletInteractorImpl.LOAD_STATE, ONE_PAGE_COUNT,
                    currentItemCount, new WalletInteractorImpl.GetHistoryListCallBack() {
                        @Override
                        public void onSuccess() {
                            getView().addHistory(currentItemCount, getInteractor().getHistoryList().size() - currentItemCount + 1,
                                    getInteractor().getHistoryList());
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void loadAndUpdateData() {
        getView().startRefreshAnimation();
        getInteractor().getHistoryList(WalletInteractorImpl.UPDATE_STATE, ONE_PAGE_COUNT,
                0, getView().getHistoryCallback());

        loadAndUpdateBalance();
    }

    @Override
    public List<History> getHistoryList() {
        return getInteractor().getHistoryList();
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
            Integer notifyPosition = getInteractor().setHistory(history);
            if (notifyPosition == null) {
                getView().notifyNewHistory();
            } else {
                getView().notifyConfirmHistory(notifyPosition);
            }
        } else {
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
        getInteractor().unSubscribe();
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