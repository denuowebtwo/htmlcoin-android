package org.qtum.wallet.model.gson.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;


public class History {

    @SerializedName("blocktime")
    @Expose
    private Long blockTime;
    @SerializedName("blockheight")
    @Expose
    private Integer blockHeight;
    @SerializedName("blockhash")
    @Expose
    private String blockHash;
    @SerializedName("txid")
    @Expose
    private String txHash;
    @SerializedName("valueIn")
    @Expose
    private BigDecimal amount;
    @SerializedName("contract_has_been_created")
    @Expose
    private Boolean contractHasBeenCreated;
    @SerializedName("vout")
    @Expose
    private List<Vout> vout = null;
    @SerializedName("vin")
    @Expose
    private List<Vin> vin = null;
    private BigDecimal changeInBalance;

    /**
     * Constructor for unit tests
     */
    public History() {

    }

    /**
     * Constructor for unit tests
     */
    public History(Long blockTime) {
        this.blockTime = blockTime;
    }

    /**
     * Constructor for unit tests
     */
    public History(Long blockTime, List<Vout> vout, List<Vin> vin, BigDecimal changeInBalance, Integer blockHeight) {
        this.blockTime = blockTime;
        this.vout = vout;
        this.vin = vin;
        this.changeInBalance = changeInBalance;
        this.blockHeight = blockHeight;
    }

    /**
     * Constructor for unit tests
     */
    public History(List<Vout> vout, List<Vin> vin, BigDecimal changeInBalance, Integer blockHeight) {
        this.vout = vout;
        this.vin = vin;
        this.changeInBalance = changeInBalance;
        this.blockHeight = blockHeight;
    }

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<Vout> getVout() {
        return vout;
    }

    public void setVout(List<Vout> vout) {
        this.vout = vout;
    }

    public List<Vin> getVin() {
        return vin;
    }

    public void setVin(List<Vin> vin) {
        this.vin = vin;
    }

    public BigDecimal getChangeInBalance() {
        return changeInBalance;
    }

    public void setChangeInBalance(BigDecimal changeInBalance) {
        this.changeInBalance = changeInBalance;
    }

    public Boolean getContractHasBeenCreated() {
        return contractHasBeenCreated;
    }

    public void setContractHasBeenCreated(Boolean contractHasBeenCreated) {
        this.contractHasBeenCreated = contractHasBeenCreated;
    }
}
