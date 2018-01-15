package org.qtum.wallet.utils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.HtmlCoinMainNetParams;
import org.bitcoinj.params.QtumTestNetParams;


public class CurrentNetParams {

    public  CurrentNetParams(){}

    public static NetworkParameters getNetParams(){
        return HtmlCoinMainNetParams.get();
    }

    public static String getUrl(){
//        return "https://walletapi-qtum-org-j21yg29m6l2i.runscope.net/";
        return "http://api-htmlcoin-com-j21yg29m6l2i.runscope.net";
    }

}
