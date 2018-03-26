package org.qtum.wallet.datastorage;


import android.content.Context;

import org.qtum.wallet.model.contract.Contract;
import org.qtum.wallet.model.contract.Token;
import org.qtum.wallet.model.gson.SmartContractInfo;
import org.qtum.wallet.model.gson.history.History;
import org.qtum.wallet.utils.ContractBuilder;
import org.qtum.wallet.utils.DateCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ContractStorage {
    private static ContractStorage sContractStorage;
    private static Context mContext;

    public static ContractStorage getInstance(Context context) {
        if (sContractStorage == null) {
            sContractStorage = new ContractStorage();
        }
        mContext = context;
        return sContractStorage;
    }

    public void updateContractStatus(History history) {
        if(history.getBlockTime() != null){

            String txHash = history.getTxHash();
            String contractAddress = ContractBuilder.generateContractAddress(txHash);

            TinyDB tinyDB = new TinyDB(mContext);

            boolean done = false;

            List<Contract> contractList = tinyDB.getContractListWithoutToken();
            for(Contract contract : contractList){
                if(contract.getContractAddress()!=null && contract.getContractAddress().equals(contractAddress)){
                    contract.setHasBeenCreated(true);
                    contract.setDate(DateCalculator.getDateInFormat(history.getBlockTime()*1000L));
                    done = true;
                    ArrayList<String> unconfirmedContractTxHashList = tinyDB.getUnconfirmedContractTxHasList();
                    unconfirmedContractTxHashList.remove(history.getTxHash());
                    tinyDB.putUnconfirmedContractTxHashList(unconfirmedContractTxHashList);
                    break;
                }
            }
            tinyDB.putContractListWithoutToken(contractList);

            if(!done){
                List<Token> tokenList = tinyDB.getTokenList();
                for(Token token : tokenList){
                    if(token.getContractAddress()!=null && token.getContractAddress().equals(contractAddress)){
                        token.setHasBeenCreated(true);
                        token.setDate(DateCalculator.getDateInFormat(history.getBlockTime()*1000L));
                        ArrayList<String> unconfirmedContractTxHashList = tinyDB.getUnconfirmedContractTxHasList();
                        unconfirmedContractTxHashList.remove(history.getTxHash());
                        tinyDB.putUnconfirmedContractTxHashList(unconfirmedContractTxHashList);
                        break;
                    }
                }
                tinyDB.putTokenList(tokenList);

            }
        }
    }

    public void updateContractInfo(SmartContractInfo smartContractInfo) {
        TinyDB tinyDB = new TinyDB(mContext);
        Token token = new Token();
        token.setContractAddress(smartContractInfo.getContractAddress());
        token.setDecimalUnits(Integer.parseInt(smartContractInfo.getDecimals()));
        token.setSymbol(smartContractInfo.getSymbol());
        token.setContractName(smartContractInfo.getName());
        token.setLastBalance(new BigDecimal(smartContractInfo.getTotalSupply()));

        tinyDB.setTokenInfo(token);
    }

}
