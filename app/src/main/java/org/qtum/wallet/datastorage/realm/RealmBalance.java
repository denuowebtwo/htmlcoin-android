package org.qtum.wallet.datastorage.realm;


import io.realm.RealmObject;

public class RealmBalance extends RealmObject {
    private String unconfirmedBalance;
    private String balance;
    private String immature;

    public String getUnconfirmedBalance() {
        return unconfirmedBalance;
    }

    public void setUnconfirmedBalance(String unconfirmedBalance) {
        this.unconfirmedBalance = unconfirmedBalance;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getImmature() {
        return immature;
    }

    public void setImmature(String immature) {
        this.immature = immature;
    }
}
