package org.qtum.wallet.dataprovider.rest_api;


import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.AddressDeviceTokenRequest;
import org.qtum.wallet.model.gson.AddressDeviceTokenResponse;
import org.qtum.wallet.model.gson.BlockChainInfo;

import org.qtum.wallet.model.gson.CallSmartContractRequest;
import org.qtum.wallet.model.gson.DGPInfo;
import org.qtum.wallet.model.gson.FeePerKb;
import org.qtum.wallet.model.gson.QstoreContractType;
import org.qtum.wallet.model.gson.call_smart_contract_response.CallSmartContractResponse;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.model.gson.history.HistoryResponse;
import org.qtum.wallet.model.gson.SendRawTransactionRequest;
import org.qtum.wallet.model.gson.SendRawTransactionResponse;
import org.qtum.wallet.model.gson.UnspentOutput;
import org.qtum.wallet.model.gson.qstore.ContractPurchase;
import org.qtum.wallet.model.gson.qstore.QSearchItem;
import org.qtum.wallet.model.gson.qstore.QstoreBuyResponse;
import org.qtum.wallet.model.gson.qstore.QstoreByteCodeResponse;
import org.qtum.wallet.model.gson.qstore.QstoreContract;
import org.qtum.wallet.model.gson.qstore.QstoreItem;
import org.qtum.wallet.model.gson.qstore.QstoreSourceCodeResponse;
import org.qtum.wallet.utils.CurrentNetParams;


import java.security.KeyStore;
import java.security.SecureRandom;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;


public class QtumService {

    private static QtumService sQtumService;
    private QtumRestService mServiceApi;

    public static QtumService newInstance() {
        if (sQtumService == null) {
            sQtumService = new QtumService();
        }
        return sQtumService;
    }

    private QtumService() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType){
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType){

                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.interceptors().add(httpLoggingInterceptor);
            client.readTimeout(180, TimeUnit.SECONDS);
            client.connectTimeout(180, TimeUnit.SECONDS);
//            client.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request original = chain.request();
//
//                    Request request = original.newBuilder()
//                            .header("Runscope-Request-Port", "3001")
//                            .method(original.method(), original.body())
//                            .build();
//
//                    return chain.proceed(request);
//                }
//            });

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "keystore_pass".toCharArray());
            sslContext.init(null, trustAllCerts, new SecureRandom());
            client.sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });

            Gson gson = new GsonBuilder().setLenient().create();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CurrentNetParams.getUrl())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(client.build())
                    .build();

            mServiceApi = retrofit.create(QtumRestService.class);
        } catch (Exception e){

        }
    }

    public Observable<List<UnspentOutput>> getUnspentOutputs(final String address) {
        return mServiceApi.getOutputsUnspent(address);
    }

    public Observable<List<UnspentOutput>> getUnspentOutputsForSeveralAddresses(final List<String> addresses) {
        String addressString = Joiner.on(",").join(addresses);
        return mServiceApi.getUnspentOutputsForSeveralAddresses(addressString);
    }

    public Observable<HistoryResponse> getHistoryListForSeveralAddresses(final List<String> addresses, final int limit, final int offset) {
        String addressString = Joiner.on(",").join(addresses);
        return mServiceApi.getHistoryListForSeveralAddresses(addressString, offset);
    }

    public Observable<List<History>> getHistoryList(final String address, final int limit, final int offset) {
        return mServiceApi.getHistoryList(address, offset);
    }

    public Observable<AddressBalance> getAddressBalance(final List<String> addresses) {
        String addressString = Joiner.on(",").join(addresses);
        return mServiceApi.getAddressBalance(addressString);
    }

    public Observable<BlockChainInfo> getBlockChainInfo() {
        return mServiceApi.getBlockChainInfo();
    }

    public Observable<FeePerKb> getEstimateFeePerKb(int nBlock) {
        return mServiceApi.getEstimateFeePerKb(nBlock);
    }

    public Observable<SendRawTransactionResponse> sendRawTransaction(final SendRawTransactionRequest sendRawTransactionRequest) {
        return mServiceApi.sendRawTransaction(sendRawTransactionRequest);
    }

    public Observable<CallSmartContractResponse> callSmartContract(String contractAddress, final CallSmartContractRequest callSmartContractRequest) {
        return mServiceApi.callSmartContract(contractAddress, callSmartContractRequest);
    }

    public Observable<History> getTransaction(final String txHash) {
        return mServiceApi.getTransaction(txHash);
    }

    public Observable<List<QstoreItem>> getTrendingNow() {
        return mServiceApi.getTrendingNow();
    }

    public Observable<List<QstoreItem>> getWatsNew() {
        return mServiceApi.getWatsNew();
    }

    public Observable<List<QSearchItem>> searchContracts(int offset, String type, String data, boolean byTag) {
        if (byTag) {
            return mServiceApi.getSearchContracts(20/*must be changed*/, offset/*get all contracts*/, type, new String[]{data});
        } else {
            return mServiceApi.getSearchContracts(20/*must be changed*/, offset/*get all contracts*/, type, data);
        }
    }

    public Observable<QstoreContract> getContractById(String id) {
        return mServiceApi.getContract(id);
    }

    public Observable<Object> getAbiByContractId(String contractId) {
        return mServiceApi.getAbiByContractId(contractId);
    }

    public Observable<QstoreBuyResponse> buyRequest(String contractId) {
        return mServiceApi.buyRequest(contractId);
    }

    public Observable<ContractPurchase> isPaidByRequestId(String contractId, String requestId) {
        return mServiceApi.isPaidByRequestId(contractId, requestId);
    }

    public Observable<DGPInfo> getDGPInfo(){
        return mServiceApi.getDGPInfo();
    }

    public Observable<QstoreSourceCodeResponse> getSourceCode(String contractId, String requestId, String accessToken){

        HashMap<String, String> body = new HashMap<>();
        body.put("request_id", requestId);
        body.put("access_token", accessToken);
        return mServiceApi.getSourceCode(contractId, body);
    }

    public Observable<QstoreByteCodeResponse> getByteCode(String contractId, String requestId, String accessToken) {
        HashMap<String, String> body = new HashMap<>();
        body.put("request_id", requestId);
        body.put("access_token", accessToken);
        return mServiceApi.getByteCode(contractId, body);
    }

    public Observable<List<QstoreContractType>> getContractTypes() {
        return mServiceApi.getContractTypes();
    }

    public Observable<AddressDeviceTokenResponse> updateDeviceToken(AddressDeviceTokenRequest addressDeviceToken) {
        return mServiceApi.updateDeviceToken(addressDeviceToken);
    }

    public Observable<AddressDeviceTokenResponse> updatePushyDeviceToken(AddressDeviceTokenRequest addressDeviceToken) {
        return mServiceApi.updatePushyDeviceToken(addressDeviceToken);
    }

    public Observable<AddressDeviceTokenResponse> deletePushyDeviceToken(AddressDeviceTokenRequest addressDeviceToken) {
        return mServiceApi.deletePushyDeviceToken(addressDeviceToken);
    }
}