package org.qtum.wallet.model.gson.call_smart_contract_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CallSmartContractResponse {
    @SerializedName("executionResult")
    @Expose
    private Item executionResult;

    /**
     * Constructor for unit testing
     */
    public CallSmartContractResponse() {

    }

    /**
     * Constructor for unit testing
     */
    public CallSmartContractResponse(List<Item> items) {

    }


    public Item getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(Item executionResult) {
        this.executionResult = executionResult;
    }
}
