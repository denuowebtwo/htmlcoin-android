package org.qtum.wallet.model.gson.history;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScriptPubKey {
    @SerializedName("addresses")
    @Expose
    private String[] addresses;

    public String[] getAddresses() {
        return addresses;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }
}
