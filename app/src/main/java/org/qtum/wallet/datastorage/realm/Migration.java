package org.qtum.wallet.datastorage.realm;


import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class Migration implements RealmMigration{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        Log.i("RealmMigration", "Migration: oldVersion=" + oldVersion + " - newVersion=" + newVersion);

    }
}
