package org.qtum.wallet.model.gson;

import com.google.gson.annotations.SerializedName;

public class AddressDeviceTokenRequest {
    @SerializedName("addresses")
    private String[] addresses;
    @SerializedName("deviceToken")
    private String deviceToken;
    @SerializedName("pushyDeviceToken")
    private String pushyDeviceToken;
    @SerializedName("platform")
    private String platform;

    public AddressDeviceTokenRequest(String[] addr, String token, String pushyDeviceToken){
        this.addresses = addr;
        this.deviceToken = token;
        this.pushyDeviceToken = pushyDeviceToken;
        this.platform = "android";
    }

}
