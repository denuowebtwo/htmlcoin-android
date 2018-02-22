package org.qtum.wallet.datastorage.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmHistory extends RealmObject {
    private Long blockTime;
    private Integer blockHeight;
    private String blockHash;
    @PrimaryKey
    private String txHash;
    private String amount;
    private Boolean contractHasBeenCreated;
    private RealmList<RealmVout> vout = null;
    private RealmList<RealmVin> vin = null;
    private String changeInBalance;

    public Long getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(Long blockTime) {
        this.blockTime = blockTime;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Boolean getContractHasBeenCreated() {
        return contractHasBeenCreated;
    }

    public void setContractHasBeenCreated(Boolean contractHasBeenCreated) {
        this.contractHasBeenCreated = contractHasBeenCreated;
    }

    public RealmList<RealmVout> getVout() {
        return vout;
    }

    public void setVout(RealmList<RealmVout> vout) {
        this.vout = vout;
    }

    public RealmList<RealmVin> getVin() {
        return vin;
    }

    public void setVin(RealmList<RealmVin> vin) {
        this.vin = vin;
    }

    public String getChangeInBalance() {
        return changeInBalance;
    }

    public void setChangeInBalance(String changeInBalance) {
        this.changeInBalance = changeInBalance;
    }
}
