package org.qtum.wallet.datastorage.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmScriptPubKey extends RealmObject {
    private RealmList<String> addresses;

    public RealmList<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(RealmList<String> addresses) {
        this.addresses = addresses;
    }
}
