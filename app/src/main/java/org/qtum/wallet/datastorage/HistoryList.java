package org.qtum.wallet.datastorage;

import android.content.Context;

import org.qtum.wallet.datastorage.realm.RealmStorage;
import org.qtum.wallet.model.gson.history.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryList {
    private static HistoryList sHistoryList;

    private List<History> mHistoryList;
    private int mTotalItem = 0;
    private RealmStorage realmStorage;
    private Context mContext;

    private HistoryList(Context context) {
        mHistoryList = new ArrayList<>();
        realmStorage = RealmStorage.getInstance(context);
        mContext = context;
    }

    public static HistoryList getInstance(Context context) {
        if (sHistoryList == null) {
            sHistoryList = new HistoryList(context);
        }
        return sHistoryList;
    }

    public void clearHistoryList() {
        sHistoryList = null;
        realmStorage.clearHistory();
    }

    public List<History> getHistoryList() {
        if (mHistoryList.size() == 0)
            mHistoryList = realmStorage.getHistories();
        return mHistoryList;
    }

    public void setHistoryList(List<History> historyList) {
        mHistoryList = historyList;

        ContractStorage contractStorage = ContractStorage.getInstance(mContext);
        for (History history: historyList) {
            realmStorage.upsertHistory(history);

            // update transactions
            if (history.isContractTransaction()) {
                contractStorage.updateContractStatus(history);
            }
        }
    }

    public int getTotalItem() {
        if (mTotalItem == 0 )
            mTotalItem = realmStorage.getHistoryCount();

        return mTotalItem;
    }

    public void setTotalItem(int totalItem) {
        this.mTotalItem = totalItem;
    }

}