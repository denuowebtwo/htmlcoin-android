package org.qtum.wallet.model.gson;


public class SendRawTransactionRequest {

    private String rawtx;
    private Integer allowHighFee;

    public SendRawTransactionRequest(String data, Integer allowHighFee){
        this.rawtx = data;
        this.allowHighFee = allowHighFee;
    }
}
