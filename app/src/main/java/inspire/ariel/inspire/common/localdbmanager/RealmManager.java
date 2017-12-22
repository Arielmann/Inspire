package inspire.ariel.inspire.common.localdbmanager;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.treatslist.Treat;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;

public class RealmManager {

    private static final String TAG = RealmManager.class.getName();
    private static RealmManager instance;
    private Realm mainThreadRealm;
    private RealmConfiguration config;

    private Realm openMainInstance() {
        try {
            return Realm.getInstance(config);
        } catch (RealmMigrationNeededException e) {
            try {
                Realm.deleteRealm(config);
                return Realm.getInstance(config);
            } catch (Exception ex) {
                Log.e(TAG, "No mainThreadRealm file to remove");
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    public static RealmManager getInstance() {
        if (instance == null) {
            instance = new RealmManager();
            instance.initConfig();
            instance.mainThreadRealm = instance.openMainInstance();
        }
        return instance;
    }

    private RealmManager() {
    }

    private void initConfig() {
        config = new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(AppNumbers.REALM_VERSION)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    //Find all objects in Treat.class
    public List<Treat> getVisibleTreats() {
        return new ArrayList<>(openMainInstance().copyFromRealm(getVisibleTreatsAsRealmResults(openMainInstance())));
    }

    private RealmResults<Treat> getVisibleTreatsAsRealmResults(Realm realm) {
            RealmResults<Treat> localTreats = realm.where(Treat.class).and()
                    .isNotNull(AppStrings.KEY_OBJECT_ID)
                    .and()
                    .equalTo(AppStrings.KEY_VISIBLE, true)
                    .findAll();
            localTreats = localTreats.sort(AppStrings.KEY_CREATED, Sort.DESCENDING);
            Log.i(TAG, "Treat list retrieved from db db. details: " + localTreats.toString());
            return localTreats;
    }

    public List<Treat> getPurchaseableTreats() {
        return new ArrayList<>(openMainInstance().copyFromRealm(getPurchasedTreatsAsRealmResults(openMainInstance())));
    }

    private RealmResults<Treat> getPurchasedTreatsAsRealmResults(Realm realm) {
            RealmResults<Treat> localTreats = realm.where(Treat.class).and()
                    .isNotNull(AppStrings.KEY_OBJECT_ID)
                    .and()
                    .greaterThan(AppStrings.KEY_USER_PURCHASES, 0)
                    .findAll();
            localTreats = localTreats.sort(AppStrings.KEY_CREATED, Sort.DESCENDING);
            Log.d(TAG, "Purcased Treat list retrieved from db db. details: " + localTreats.toString());
            return localTreats;
    }

    public void saveTreat(Treat treatForSaving) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                final Treat realmTreat = realm.createObject(Treat.class);
                realm.executeTransactionAsync(realm1 -> realm1.insertOrUpdate(treatForSaving));
                Log.d(TAG, "treat saved to db. details: " + realmTreat.toString());
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    //Todo: catch all possible exceptions instead of the generic 'Exception'
    public void saveTreatsList(Collection<Treat> treatsForSaving) {
        mainThreadRealm = openMainInstance();
        try {
            mainThreadRealm.executeTransactionAsync(realm -> {
                for (Treat treatForSaving : treatsForSaving) {
                    realm.insertOrUpdate(treatForSaving);
                }

                Log.d(TAG, "Server treats were saved in database. saved list size: " + treatsForSaving.size());
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    public void deleteTreat(Treat treat) {
        if (treat.getUserPurchases() > 0) { //Can't delete if treat was purchased
            Log.i(TAG, "Treat with text: " + treat.getText() + " was already purchased " + treat.getUserPurchases() + " times and won't be deleted from local data base");
            return;
        }
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                RealmResults<Treat> result = realm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, treat.getObjectId()).findAll();
                result.deleteFirstFromRealm();
                Log.i(TAG, "treat deleted from db. details: " + treat.toString());
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    public void deleteAllTreats() {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                RealmResults<Treat> results = realm.where(Treat.class).findAll();
                if (!results.isEmpty()) {
                    results.deleteAllFromRealm();
                    Log.d(TAG, "All treats were deleted from realm");
                }
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    public void syncRealmWithServerTreats(List<Treat> serverTreats) {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm1 -> {
                try {
                    RealmResults<Treat> localTreats = RealmManager.getInstance().getVisibleTreatsAsRealmResults(realm1);
                    if (localTreats == null || serverTreats == null) {
                        Log.e(AppStrings.REALM_SYNC_SERVER_TAG, "Sync failed. One of the quotes list is null. Server list: " + serverTreats + " local db list: " + localTreats);
                        return;
                    }
                    //Convert serverTreats to hashMap
                    Map<String, Treat> serverTreatsMap = convertTreatListToMap(serverTreats);

                    for (final Treat localTreat : localTreats) {
                        String objectId = localTreat.getObjectId();
                        Treat match = serverTreatsMap.get(objectId);
                        if (match != null) {
                            // Treat exists in Realm. Remove from server treats map to prevent insert later.
                            serverTreatsMap.remove(objectId);
                            updateIfNeeded(realm1, match, localTreat);
                        } else {
                            // Entry doesn't exist. Remove it from the database if it was never purchased.
                            if (localTreat.getUserPurchases() > 0) {
                                Log.d(TAG, "Treat with text: " + localTreat.getText() + " was already purchased " + localTreat.getUserPurchases() + " times and won't be deleted from local data base");
                                return;
                            }
                            RealmResults<Treat> results = realm1.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, localTreat.getObjectId()).findAll();
                            results.deleteFirstFromRealm();
                            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Treat deleted from db. details: " + localTreat.toString());
                        }
                    }
                    saveTreatsList(serverTreatsMap.values());
                } finally {
                    realm1.close();
                }
            });
    }

    public void updatePurchasedTreatsInDb(List<Treat> serverTreats) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm1 -> {
                RealmResults<Treat> localTreats = RealmManager.getInstance().getVisibleTreatsAsRealmResults(realm1);
                if (localTreats == null || serverTreats == null) {
                    Log.e(AppStrings.REALM_SYNC_SERVER_TAG, "Sync failed. One of the lists is null. Server list: " + serverTreats + " local db list: " + localTreats);
                    return;
                }
                Map<String, Treat> serverTreatsMap = convertTreatListToMap(serverTreats);

                for (final Treat localTreat : localTreats) {
                    String objectId = localTreat.getObjectId();
                    Treat match = serverTreatsMap.get(objectId);
                    if ((match != null)) {
                        serverTreatsMap.remove(objectId); // Treat exists in Realm. Remove from server treats map to prevent insert later.
                        updateIfNeeded(realm1, match, localTreat);
                    }
                }
                saveTreatsList(serverTreatsMap.values());

            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    private void updateIfNeeded(Realm realm, Treat serverTreat, Treat localTreat) {
        Treat finalLocalTreat = realm.copyFromRealm(localTreat);
        if (!serverTreat.equals(finalLocalTreat)) {
            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Scheduling update for treat: " + serverTreat.toString());
            realm.insertOrUpdate(serverTreat);
        } else {
            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Treat equality in databases. no update required for " + serverTreat.toString());
        }
    }

    public void updateTreat(Treat newTreat) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                realm.executeTransactionAsync(realm1 -> realm.insertOrUpdate(newTreat));
                Log.d(TAG, "Treat updated in db. details: " + newTreat);
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }

    }


    private Map<String, Treat> convertTreatListToMap(List<Treat> serverTreats) {
        HashMap<String, Treat> serverTreatsMap = new HashMap<>();
        for (Treat treat : serverTreats) {
            serverTreatsMap.put(treat.getObjectId(), treat);
        }
        return serverTreatsMap;
    }
}

    private void updateRealmTreatFromOtherTreat(Treat realmTreat, Treat otherTreat) {
        realmTreat.setObjectId(otherTreat.getObjectId());
        realmTreat.setBgImageName(otherTreat.getBgImageName());
        realmTreat.setCreated(otherTreat.getCreated());
        realmTreat.setFontPath(otherTreat.getFontPath());
        realmTreat.setOwnerId(otherTreat.getOwnerId());
        realmTreat.setText(otherTreat.getText());
        realmTreat.setTextColor(otherTreat.getTextColor());
        realmTreat.setTextSize(otherTreat.getTextSize());
        realmTreat.setPurchasesLimit(otherTreat.getPurchasesLimit());
        //realmTreat.setUserPurchases(otherTreat.getUserPurchases()); //Excluded on purpose
        realmTreat.setAllPurchases(otherTreat.getAllPurchases());
        realmTreat.setPurchaseable(otherTreat.getIsPurchaseable());
        Log.d(TAG, "Treat updated in db. details: " + realmTreat);
    }


