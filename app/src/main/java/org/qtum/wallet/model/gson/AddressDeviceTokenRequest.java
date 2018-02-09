package org.qtum.wallet.model.gson;

import com.google.gson.annotations.SerializedName;

public class AddressDeviceTokenRequest {
    @SerializedName("addresses")
    private String[] addresses;
    @SerializedName("deviceToken")
    private String deviceToken;

    public AddressDeviceTokenRequest(String[] addr, String token){
        this.addresses = addr;
        this.deviceToken = token;
    }

}
