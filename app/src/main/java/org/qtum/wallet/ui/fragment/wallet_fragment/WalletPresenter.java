package org.qtum.wallet.ui.fragment.wallet_fragment;

import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.ui.base.base_fragment.BaseFragmentPresenter;

import java.util.List;

public interface WalletPresenter extends BaseFragmentPresenter {
    void onRefresh();

    void openTransactionFragment(int position);

    void onLastItem(int currentItemCount);

    void notifyHeader();

    List<History> getHistoryList();

    void onNetworkStateChanged(boolean networkConnectedFlag);

    Boolean getNetworkConnectedFlag();

    void onNewHistory(History history);

    boolean getVisibility();

    void updateVisibility(boolean value);
}
