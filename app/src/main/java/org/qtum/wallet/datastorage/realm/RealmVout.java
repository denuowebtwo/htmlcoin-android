package org.qtum.wallet.datastorage.realm;


import org.qtum.wallet.model.gson.history.ScriptPubKey;

import java.math.BigDecimal;

import io.realm.RealmObject;

public class RealmVout extends RealmObject {
    private String value;
    private String address;
    private RealmScriptPubKey scriptPubKey;

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
}
