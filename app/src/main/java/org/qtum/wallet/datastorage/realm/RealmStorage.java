package org.qtum.wallet.datastorage.realm;


import android.content.Context;
import android.util.Log;

import org.qtum.wallet.datastorage.KeyStorage;
import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.model.gson.history.ScriptPubKey;
import org.qtum.wallet.model.gson.history.Vin;
import org.qtum.wallet.model.gson.history.Vout;
import org.qtum.wallet.utils.LogUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmStorage {
    private final static String LOG_TAG = "RealmStorage";

    private static RealmStorage instance;
    private Context mContext;
    RealmConfiguration config;

    public static RealmStorage getInstance(Context context) {
        if (instance == null) {
            instance = new RealmStorage(context);
        }
        return instance;
    }

    private RealmStorage(Context context) {
        Realm.init(context);
        config = new RealmConfiguration.Builder()
                .name("htmlcoin.realm")
                .schemaVersion(2)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(config);
        this.mContext = context.getApplicationContext();
    }

    private History toHistory(RealmHistory realmHistory) {
        History history = new History();
        history.setBlockTime(realmHistory.getBlockTime());
        history.setBlockHeight(realmHistory.getBlockHeight());
        history.setBlockHash(realmHistory.getBlockHash());
        history.setTxHash(realmHistory.getTxHash());
        history.setAmount(new BigDecimal(realmHistory.getAmount()));
        if (realmHistory.getChangeInBalance() != null)
            history.setChangeInBalance(new BigDecimal(realmHistory.getChangeInBalance()));

        List<Vout> voutList = new ArrayList<>();
        for(RealmVout realmVout: realmHistory.getVout()) {
            Vout vout = new Vout();
            vout.setAddress(realmVout.getAddress());
            vout.setValue(new BigDecimal(realmVout.getValue()));

            ScriptPubKey scriptPubKey = new ScriptPubKey();
            String[] addressArray = new String[realmVout.getScriptPubKey().getAddresses().size()];
            realmVout.getScriptPubKey().getAddresses().toArray(addressArray);
            scriptPubKey.setAddresses(addressArray);

            vout.setScriptPubKey(scriptPubKey);
            vout.setSpentTxId(realmVout.getSpentTxId());

            voutList.add(vout);
        }
        history.setVout(voutList);

        List<Vin> vinList = new ArrayList<>();
        for (RealmVin realmVin: realmHistory.getVin()) {
            Vin vin = new Vin();
            vin.setAddress(realmVin.getAddress());
            vin.setValue(new BigDecimal(realmVin.getValue()));
            vin.setOwnAddress(realmVin.isOwnAddress());

            vinList.add(vin);
        }
        history.setVin(vinList);

        return history;
    }

    public void upsertHistory(History history) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(config);
            realm.beginTransaction();
            RealmHistory realmHistory = realm.where(RealmHistory.class)
                    .equalTo("txHash", history.getTxHash())
                    .findFirst();

            if (realmHistory == null) {
                realmHistory = realm.createObject(RealmHistory.class, history.getTxHash());
                realmHistory.setAmount(history.getAmount().toString());
                realmHistory.setBlockHash(history.getBlockHash());
                realmHistory.setBlockTime(history.getBlockTime());
                realmHistory.setBlockHeight(history.getBlockHeight());
                if (history.getContractHasBeenCreated() != null)
                    realmHistory.setContractHasBeenCreated(history.getContractHasBeenCreated());
                else
                    realmHistory.setContractHasBeenCreated(true);
                if (history.getChangeInBalance() != null)
                    realmHistory.setChangeInBalance(history.getChangeInBalance().toString());

                RealmList<RealmVout> realmVoutRealmList = new RealmList<>();
                for (Vout vout : history.getVout()) {
                    RealmVout realmVout = realm.createObject(RealmVout.class);
                    if (vout.getAddress() != null)
                        realmVout.setAddress(vout.getAddress());

                    if (vout.getValue() != null)
                        realmVout.setValue(vout.getValue().toString());

                    RealmScriptPubKey realmScriptPubKey = realm.createObject(RealmScriptPubKey.class);
                    RealmList<String> realmList = new RealmList<>(vout.getAddress());
                    realmScriptPubKey.setAddresses(realmList);

                    realmVout.setScriptPubKey(realmScriptPubKey);
                    if (vout.getSpentTxId() != null)
                        realmVout.setSpentTxId(vout.getSpentTxId());

                    realmVoutRealmList.add(realmVout);
                }
                realmHistory.setVout(realmVoutRealmList);

                RealmList<RealmVin> realmVinRealmList = new RealmList<>();
                for (Vin vin: history.getVin()) {
                    RealmVin realmVin = realm.createObject(RealmVin.class);
                    if (vin.getAddress() != null)
                        realmVin.setAddress(vin.getAddress());
                    if (vin.getValue() != null)
                        realmVin.setValue(vin.getValue().toString());

                    realmVin.setOwnAddress(vin.isOwnAddress());

                    realmVinRealmList.add(realmVin);
                }
                realmHistory.setVin(realmVinRealmList);

            } else {
                for (Vout vout : history.getVout()) {
                    for(RealmVout realmVout: realmHistory.getVout()){
                        if (vout.getAddress().equals(realmVout.getAddress()) && vout.getSpentTxId() != null) {
                            realmVout.setSpentTxId(vout.getSpentTxId());
                        }
                    }

                }
            }
            realm.commitTransaction();
        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public int getHistoryCount() {
        Realm realm = null;
        try {
            realm = Realm.getInstance(config);
            long count = realm.where(RealmHistory.class)
                    .count();
            return (int)count;

        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }

        return 0;
    }
    public List<History> getHistories() {
        Realm realm = null;
        List<History> historyList = new ArrayList<>();
        try {
            realm = Realm.getInstance(config);
            RealmResults<RealmHistory> realmQuery = realm.where(RealmHistory.class)
                    .sort("blockTime", Sort.DESCENDING)
                    .findAll();
            for(RealmHistory realmHistory: realmQuery) {
                historyList.add(toHistory(realmHistory));
            }

        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }

        return historyList;
    }

    public Boolean checkSpentOutput(String txHash, String address) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(config);

            RealmResults<RealmHistory> realmQuery = realm.where(RealmHistory.class)
                    .equalTo("txHash", txHash)
                    .findAll();

            for (RealmHistory realmHistory: realmQuery) {
                for(RealmVout vout: realmHistory.getVout()){
                    if(address.equals(vout.getAddress()) && vout.getSpentTxId() != null){
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }

        return false;
    }

    public BigDecimal getUnconfirmedOutputBalance() {
        Realm realm = null;
        BigDecimal bigDecimal = BigDecimal.ZERO;
        try {
            realm = Realm.getInstance(config);

            RealmResults<RealmHistory> realmQuery = realm.where(RealmHistory.class)
                    .isNull("blockTime")
                    .findAll();
            for (RealmHistory realmHistory: realmQuery) {
                for (RealmVout realmVout: realmHistory.getVout()) {
                    if(KeyStorage.getInstance().getAddresses().contains(realmVout.getAddress()))
                        bigDecimal = bigDecimal.add(new BigDecimal(realmVout.getValue()));
                }
            }

        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }

        return bigDecimal;
    }

    // Balance
    public AddressBalance getAddressBalance() {
        Realm realm = null;
        AddressBalance addressBalance = null;
        try {
            realm = Realm.getInstance(config);
            RealmBalance realmBalance = realm.where(RealmBalance.class)
                    .findFirst();
            if (realmBalance != null) {
                addressBalance = new AddressBalance();
                addressBalance.setBalance(new BigDecimal(realmBalance.getBalance()));
                addressBalance.setUnconfirmedBalance(new BigDecimal(realmBalance.getUnconfirmedBalance()));
                addressBalance.setImmature(new BigDecimal(realmBalance.getImmature()));
            }
        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }

        return addressBalance;
    }

    public void updateAddressBalance(AddressBalance addressBalance) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(config);
            realm.beginTransaction();

            RealmBalance realmBalance = realm.where(RealmBalance.class)
                    .findFirst();
            if (realmBalance == null)
                realmBalance = realm.createObject(RealmBalance.class);

            realmBalance.setBalance(addressBalance.getBalance().toString());
            realmBalance.setUnconfirmedBalance(addressBalance.getUnconfirmedBalance().toString());
            realmBalance.setImmature(addressBalance.getImmature().toString());

            realm.commitTransaction();

        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public void clearHistory() {
        Realm realm = null;
        try {
            realm = Realm.getInstance(config);
            realm.beginTransaction();

            realm.where(RealmVin.class).findAll().deleteAllFromRealm();
            realm.where(RealmScriptPubKey.class).findAll().deleteAllFromRealm();
            realm.where(RealmVout.class).findAll().deleteAllFromRealm();
            realm.where(RealmHistory.class).findAll().deleteAllFromRealm();
            realm.where(RealmBalance.class).findAll().deleteAllFromRealm();

            realm.commitTransaction();
        } catch (Exception ex) {
            LogUtils.error(LOG_TAG, ex.getMessage());
            LogUtils.error(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            if (realm != null)
                realm.close();
        }
    }
}
