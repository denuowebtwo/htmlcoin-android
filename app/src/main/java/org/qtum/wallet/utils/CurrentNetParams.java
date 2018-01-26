package org.qtum.wallet.utils;

import org.bitcoinj.core.NetworkParameters;
import org.qtum.wallet.BuildConfig;


public class CurrentNetParams {

    public  CurrentNetParams(){}

    public static NetworkParameters getNetParams(){
        if(BuildConfig.FLAVOR.equalsIgnoreCase("dev")) {
            return HtmlCoinRegTestParams.get();
        } else if(BuildConfig.FLAVOR.equalsIgnoreCase("stagingTestnet")) {
            return HtmlCoinTestNetParams.get();
        } else if(BuildConfig.FLAVOR.equalsIgnoreCase("stagingRegtest")) {
            return HtmlCoinRegTestParams.get();
        } else {
            return HtmlCoinMainNetParams.get();
        }

    }

    public static String getUrl(){
//        return "https://walletapi-qtum-org-j21yg29m6l2i.runscope.net/";
//        return "http://explorer-htmlcoin-com-j21yg29m6l2i.runscope.net";
        return BuildConfig.SERVER_URL;
    }

}
