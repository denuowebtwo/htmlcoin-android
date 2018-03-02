package org.qtum.wallet.ui.base;


import org.qtum.wallet.model.AddressWithBalance;
import org.qtum.wallet.model.gson.AddressBalance;
import org.qtum.wallet.model.gson.UnspentOutput;

import java.math.BigDecimal;
import java.util.List;

import rx.Observable;

public interface AddressInteractor {
    List<String> getAddresses();

    Observable<List<UnspentOutput>> getUnspentOutputs(List<String> addresses);

    Observable<List<AddressWithBalance>> getAddressBalances(List<String> addresses);

    Observable<AddressBalance> getAddressBalance(List<String> addresses);

    Boolean updateAddressDeviceToken(String[] addresses, String token);

    Boolean updatePushyDeviceToken(String[] addresses, String token);
}
