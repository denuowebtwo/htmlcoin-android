package org.qtum.wallet.dataprovider.rest_api;

import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.AddressDeviceTokenRequest;
import org.qtum.wallet.model.gson.AddressDeviceTokenResponse;
import org.qtum.wallet.model.gson.BlockChainInfo;

import org.qtum.wallet.model.gson.CallSmartContractRequest;
import org.qtum.wallet.model.gson.ContractParams;
import org.qtum.wallet.model.gson.DGPInfo;
import org.qtum.wallet.model.gson.FeePerKb;
import org.qtum.wallet.model.gson.QstoreContractType;
import org.qtum.wallet.model.gson.SmartContractInfo;
import org.qtum.wallet.model.gson.call_smart_contract_response.CallSmartContractResponse;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.model.gson.history.HistoryResponse;
import org.qtum.wallet.model.gson.SendRawTransactionRequest;
import org.qtum.wallet.model.gson.SendRawTransactionResponse;
import org.qtum.wallet.model.gson.UnspentOutput;
import org.qtum.wallet.model.gson.history.TransactionReceipt;
import org.qtum.wallet.model.gson.qstore.ContractPurchase;
import org.qtum.wallet.model.gson.qstore.QSearchItem;
import org.qtum.wallet.model.gson.qstore.QstoreBuyResponse;
import org.qtum.wallet.model.gson.qstore.QstoreByteCodeResponse;
import org.qtum.wallet.model.gson.qstore.QstoreContract;
import org.qtum.wallet.model.gson.qstore.QstoreItem;
import org.qtum.wallet.model.gson.qstore.QstoreSourceCodeResponse;
import org.qtum.wallet.model.gson.token_history.TokenHistoryResponse;

import java.util.HashMap;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


interface QtumRestService {

//    @GET("/outputs/unspent/{address}")
    @GET("/api/addrs/{address}/unspent")
    Observable<List<UnspentOutput>> getOutputsUnspent(@Path("address") String address);

//    @GET("/history/{address}/{limit}/{offset}")
    @GET("/api/addrs/{address}/txs")
    Observable<List<History>> getHistoryList(@Path("address") String address, @Query("from") int from);

    @GET("/api/status")
    Observable<BlockChainInfo> getBlockChainInfo();

    @POST("/api/tx/send")
    Observable<SendRawTransactionResponse> sendRawTransaction(@Body SendRawTransactionRequest sendRawTransactionRequest);

//    @GET("/outputs/unspent")
    @GET("/api/addrs/{addresses}/unspent")
    Observable<List<UnspentOutput>> getUnspentOutputsForSeveralAddresses(@Path("addresses") String addresses);

//    @GET("/history/{limit}/{offset}")
    @GET("/api/addrs/{addresses}/txs")
    Observable<HistoryResponse> getHistoryListForSeveralAddresses(@Path("addresses") String addresses, @Query("from") int from);

//    @GET("/transactions/{tx_hash}")
    @GET("/api/tx/{tx_hash}")
    Observable<History> getTransaction(@Path("tx_hash") String txHash);

    @GET("/api/contracts/{addressContract}/hash/{contractHash}/call")
    Observable<CallSmartContractResponse> callSmartContractInfo(@Path("addressContract") String addressContract, @Path("contractHash")String contractHash, @Query("from") String from);

    @GET("/contracts/trending-now")
    Observable<List<QstoreItem>> getTrendingNow();

    @GET("/contracts/last-added")
    Observable<List<QstoreItem>> getWatsNew();

    @GET("/contracts/{count}/{offset}")
    Observable<List<QSearchItem>> getSearchContracts(@Path("count") int count, @Path("offset") int offset, @Query("type") String type, @Query("tags[]") String[] tags); //by tag

    @GET("/contracts/{count}/{offset}")
    Observable<List<QSearchItem>> getSearchContracts(@Path("count") int count, @Path("offset") int offset, @Query("type") String type, @Query("name") String name); //by name


     @GET("/contracts/{contract_id}")
    Observable<QstoreContract> getContract(@Path("contract_id") String contractId);

    @POST("/contracts/{contract_id}/source-code")
    Observable<QstoreSourceCodeResponse> getSourceCode(@Path("contract_id") String contractId, @Body HashMap<String, String> body);

    @POST("/contracts/{contract_id}/bytecode")
    Observable<QstoreByteCodeResponse> getByteCode(@Path("contract_id") String contractId, @Body HashMap<String, String> body);

    @GET("/contracts/{contract_id}/abi")
    Observable<Object> getAbiByContractId(@Path("contract_id") String contractId);

    @POST("/contracts/{contract_id}/buy-request")
    Observable<QstoreBuyResponse> buyRequest(@Path("contract_id") String contractId);

    @GET("/contracts/{contract_id}/is-paid/by-request-id")
    Observable<ContractPurchase> isPaidByRequestId(@Path("contract_id") String contractId, @Query("request_id") String requestId);

    @GET("/api/utils/minestimatefee")
    Observable<FeePerKb> getEstimateFeePerKb(@Query("nBlocks") int nBlocks);

    @GET("/contracts/types")
    Observable<List<QstoreContractType>> getContractTypes();

    @GET("/api/erc20/{address}")
    Observable<SmartContractInfo> getContractInfo(@Path("address") String address);

    @GET("/api/erc20/{contractAddress}/transfers")
    Observable<TokenHistoryResponse> getTokenHistoryList(@Path("contractAddress") String contractAddress, @Query("limit") int limit, @Query("offset") int offset, @Query("addresses[]") List<String> addresses);

    @GET("/api/txs/{txhash}/receipt")
    Observable<List<TransactionReceipt>> getTransactionReceipt(@Path("txhash") String txHash);

    @GET("/api/contracts/{addressContract}/params?keys=symbol,decimals,name,totalSupply")
    Observable<ContractParams> getContractParams(@Path("addressContract") String addressContract);

    @GET("/api/dgpinfo")
    Observable<DGPInfo> getDGPInfo();

    @GET("/api/addrs/{addresses}/balance")
    Observable<AddressBalance> getAddressBalance(@Path("addresses") String addresses);

    @POST("/notification/devicetoken/create")
    Observable<AddressDeviceTokenResponse> updateDeviceToken(@Body AddressDeviceTokenRequest addressDeviceToken);

    @POST("/notification/devicetoken/pushy/create")
    Observable<AddressDeviceTokenResponse> updatePushyDeviceToken(@Body AddressDeviceTokenRequest addressDeviceToken);

    @POST("/notification/devicetoken/pushy/delete")
    Observable<AddressDeviceTokenResponse> deletePushyDeviceToken(@Body AddressDeviceTokenRequest addressDeviceToken);
}