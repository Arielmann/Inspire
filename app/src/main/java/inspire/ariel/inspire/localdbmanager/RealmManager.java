package inspire.ariel.inspire.localdbmanager;

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

    //find all objects in Treat.class
    public List<Treat> getTreats() {
        return new ArrayList<>(getTreats(openMainInstance()));
    }

    private RealmResults<Treat> getTreats(Realm realm) {
        try {
            realm = openMainInstance();
            RealmResults<Treat> localTreats = realm.where(Treat.class).and().isNotNull(AppStrings.KEY_OBJECT_ID).findAll();
            localTreats = localTreats.sort(AppStrings.KEY_CREATED, Sort.DESCENDING);
            Log.d(TAG, "treat list retrieved from db db. details: " + localTreats.toString());
            return localTreats;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public void saveTreat(Treat treatForSaving) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                final Treat realmTreat = realm.createObject(Treat.class);
                realm.executeTransactionAsync(realm1 -> updateRealmTreatFromOtherTreat(realmTreat, treatForSaving));
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
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                for (Treat treatForSaving : treatsForSaving) {
                    final Treat realmTreat = realm.createObject(Treat.class);
                    updateRealmTreatFromOtherTreat(realmTreat, treatForSaving);
                }
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
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
        realmTreat.setTimesPurchased(otherTreat.getTimesPurchased());
        Log.d(TAG, "Treat updated in db. details: " + realmTreat);
    }

    public void updateTreat(Treat upToDateTreat) {
        mainThreadRealm = openMainInstance();
        mainThreadRealm.executeTransactionAsync(realm -> {
            final Treat realmTreat = realm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, upToDateTreat.getObjectId()).findFirst();
            realm.executeTransactionAsync(realm1 -> updateRealmTreatFromOtherTreat(realmTreat, upToDateTreat));
            Log.d(TAG, "Treat updated in db. details: " + realmTreat);
        });
    }

    public void deleteTreat(Treat treat) {
        if(treat.getTimesPurchased() > 0){ //Can't delete if treat was purchased
            Log.d(TAG, "Treat with text: " + treat.getText() + " was already purchased " + treat.getTimesPurchased() + " times and won't be deleted from local data base");
            return;
        }
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                RealmResults<Treat> result = realm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, treat.getObjectId()).findAll();
                result.deleteFirstFromRealm();
                Log.d(TAG, "treat deleted from db. details: " + treat.toString());
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
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm1 -> {
                try {
                    RealmResults<Treat> localTreats = RealmManager.getInstance().getTreats(realm1);
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
                            updateIfNeeded(match, localTreat);
                        } else {
                            // Entry doesn't exist. Remove it from the database if it was never purchased.
                            if(localTreat.getTimesPurchased() > 0){
                                Log.d(TAG, "Treat with text: " + localTreat.getText() + " was already purchased " + localTreat.getTimesPurchased() + " times and won't be deleted from local data base");
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
        } finally {
            mainThreadRealm.close();
        }
    }

    public void updatePurchasedTreatsInDb(List<Treat> serverTreats) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm1 -> {
                try {
                    RealmResults<Treat> localTreats = RealmManager.getInstance().getTreats(realm1);
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
                            updateIfNeeded(match, localTreat);
                        }
                    }
                    saveTreatsList(serverTreatsMap.values());

                } finally {
                    realm1.close();
                }
            });
        } finally {
            mainThreadRealm.close();
        }
    }

    private void updateIfNeeded(Treat serverTreat, Treat localTreat) {
        if (!serverTreat.equals(localTreat)) {
            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Scheduling update for treat: " + serverTreat.toString());
            updateRealmTreatFromOtherTreat(localTreat, serverTreat);
        } else {
            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Treat equality in databases. no update required for " + serverTreat.toString());
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


  /*
    void startSyncProcess(List<Treat> serverTreats) {
        Realm bgRealm = openMainInstance();
        try {
            if (localTreats == null) {
                Log.e(AppStrings.REALM_SYNC_SERVER_TAG, "Data base quotes is null, maybe it was not queried or it is empty");
                return;
            }
            //Convert serverTreats to hashMap
            HashMap<String, Treat> serverTreatsMap = new HashMap<>();
            for (Treat treat : serverTreats) {
                serverTreatsMap.put(treat.getObjectId(), treat);
            }

            bgRealm.beginTransaction();
            for (final Treat localTreat : localTreats) {
                String objectId = localTreat.getObjectId();
                Treat match = serverTreatsMap.get(objectId);
                if (match != null) {
                    // Treat exists in Realm. Remove from server treats map to prevent insert later.
                    serverTreatsMap.remove(objectId);
                    // Check to see if the treat needs to be updated
                    if (!match.equals(localTreat)) {
                        Log.i(AppStrings.REALM_SYNC_SERVER_TAG, "Scheduling update for treat: " + match.toString());
                        updateRealmTreatFromOtherTreat(localTreat, match);
                    } else {
                        Log.i(AppStrings.REALM_SYNC_SERVER_TAG, "Treat equality in databases. no update required for " + match.toString());
                    }
                } else {
                    // Entry doesn't exist. Remove it from the database.
                    RealmResults<Treat> result = bgRealm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, localTreat.getObjectId()).findAll();
                    result.deleteFirstFromRealm();
                    Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "treat removed from db. details: " + localTreat.toString());
                }

                saveTreatsList(serverTreatsMap.values());
            }
            bgRealm.commitTransaction();

        } catch (RealmException e) {
            e.printStackTrace();
            bgRealm.cancelTransaction();
        } catch (Exception e) {
            bgRealm.cancelTransaction();
            e.printStackTrace();
        } finally {
            bgRealm.close();
        }
    }
*/

/*
    public class RealmSyncService extends IntentService {

        public RealmSyncService() {
            super(AppStrings.REALM_SYNC_SERVER_TAG);
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            Realm bgRealm = openMainInstance();
            try {
                assert intent != null;
                List<Treat> serverTreats = intent.getParcelableArrayListExtra(AppStrings.KEY_SERVER_TREATS);

                if(serverTreats == null){
                    Log.e(AppStrings.REALM_SYNC_SERVER_TAG, "No server treats where sent tp service");
                    return;
                }
                //Convert serverTreats to hashMap
                HashMap<String, Treat> serverTreatsMap = new HashMap<>();
                for (Treat treat : serverTreats) {
                    serverTreatsMap.put(treat.getObjectId(), treat);
                }

                RealmResults<Treat> localTreats = instance.getTreats(bgRealm);
                bgRealm.beginTransaction();
                for (final Treat localTreat : localTreats) {
                    String objectId = localTreat.getObjectId();
                    Treat match = serverTreatsMap.get(objectId);
                    if (match != null) {
                        // Treat exists in Realm. Remove from server treats map to prevent insert later.
                        serverTreatsMap.remove(objectId);
                        // Check to see if the treat needs to be updated
                        if (!match.equals(localTreat)) {
                            Log.i(AppStrings.REALM_SYNC_SERVER_TAG, "Scheduling update for treat: " + match.toString());
                            updateRealmTreatFromOtherTreat(localTreat, match);
                        } else {
                            Log.i(AppStrings.REALM_SYNC_SERVER_TAG, "Treat equality in databases. no update required for " + match.toString());
                        }
                    } else {
                        // Entry doesn't exist. Remove it from the database.
                        RealmResults<Treat> result = bgRealm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, localTreat.getObjectId()).findAll();
                        result.deleteFirstFromRealm();
                        Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "treat removed from db. details: " + localTreat.toString());
                    }

                    saveTreatsList(serverTreatsMap.values());
                }
                bgRealm.commitTransaction();

            } catch (RealmException e) {
                e.printStackTrace();
                bgRealm.cancelTransaction();
            } catch (Exception e) {
                bgRealm.cancelTransaction();
                e.printStackTrace();
            } finally {
                bgRealm.close();
            }
        }
    }*/









/*   //query a single item startOperations the given objectId
    public Treat getTreat(String objectId) {
        try {
            mainThreadRealm = getRealm();
            Treat dbTreat = mainThreadRealm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, objectId).findFirst();
            Log.d(TAG, "treat retrieved from db db. details: " + dbTreat);
            return dbTreat;
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }*/

/*
    //clear all objects from Treat.class
    public void removeAll(Class<? extends RealmModel> removedClass) {
        try {
            mainThreadRealm = getRealm();
            mainThreadRealm.executeTransactionAsync(mainThreadRealm -> mainThreadRealm.delete(removedClass));
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }*/

 /*   //Refresh the mainThreadRealm instance
    public void refresh() {
        Realm mainThreadRealm = null;
        try {
            mainThreadRealm = getRealm();
            mainThreadRealm.refresh();
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }
*/
   /* //query example
    public RealmResults<Treat> queryTreats() {

        Realm mainThreadRealm = null;
        try {
            mainThreadRealm = getRealm();
              return mainThreadRealm.where(Treat.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }*/
