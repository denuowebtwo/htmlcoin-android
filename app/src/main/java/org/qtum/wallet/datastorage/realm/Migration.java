package org.qtum.wallet.datastorage.realm;


import android.util.Log;

import org.qtum.wallet.utils.LogUtils;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Migration implements RealmMigration{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        LogUtils.info("RealmMigration", "Migration: oldVersion=" + oldVersion + " - newVersion=" + newVersion);

        if (oldVersion == 1) {
            schema.get("RealmVout")
                    .addField("spentTxId", String.class);
            oldVersion++;
        }
    }
}
