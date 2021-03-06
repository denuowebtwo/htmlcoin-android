package org.qtum.wallet.datastorage.realm;


import io.realm.RealmObject;

public class RealmVout extends RealmObject {
    private String value;
    private String address;
    private RealmScriptPubKey scriptPubKey;
    private String spentTxId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RealmScriptPubKey getScriptPubKey() {
        return scriptPubKey;
    }

    public void setScriptPubKey(RealmScriptPubKey scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    public String getSpentTxId() {
        return spentTxId;
    }

    public void setSpentTxId(String spentTxId) {
        this.spentTxId = spentTxId;
    }
}
