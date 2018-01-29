package org.qtum.wallet.model.gson;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;


public class AddressBalance {
    @SerializedName("unconfirmedBalance")
    private BigDecimal unconfirmedBalance;

    @SerializedName("balance")
    private BigDecimal balance;

    @SerializedName("immature")
    private BigDecimal immature;

    public AddressBalance() {

    }

    public BigDecimal getUnconfirmedBalance() {
        if (unconfirmedBalance.compareTo(BigDecimal.ZERO) != 0 )
            return unconfirmedBalance.movePointLeft(8);
        return unconfirmedBalance;
    }

    public void setUnconfirmedBalance(BigDecimal unconfirmedBalance) {
        this.unconfirmedBalance = unconfirmedBalance;
    }

    public BigDecimal getBalance() {
        if (balance.compareTo(BigDecimal.ZERO) != 0 )
            return balance.movePointLeft(8);
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getImmature() {
        if (immature.compareTo(BigDecimal.ZERO) > 0 )
            return immature.movePointLeft(8);
        return immature;
    }

    public void setImmature(BigDecimal immature) {
        this.immature = immature;
    }
}
