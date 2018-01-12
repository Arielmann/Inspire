package inspire.ariel.inspire.common.localdbmanager;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hugo.weaving.DebugLog;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmMigrationNeededException;

@DebugLog
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
                .equalTo(AppStrings.KEY_VISIBLE, true) //Everything visible
                .or()
                .isNotNull(AppStrings.KEY_OBJECT_ID)
                .and()
                .greaterThan(AppStrings.KEY_USER_PURCHASES, 0) //Every purchased treat
                .findAll();
        localTreats = localTreats.sort(AppStrings.KEY_CREATED, Sort.DESCENDING);
        Log.i(TAG, "Treat list retrieved from db. details: " + localTreats.toString());
        return localTreats;
    }

    public List<Treat> getPurchasedTreats() {
        return new ArrayList<>(openMainInstance().copyFromRealm(getPurchasedTreatsAsRealmResults(openMainInstance())));
    }

    private RealmResults<Treat> getPurchasedTreatsAsRealmResults(Realm realm) {
        RealmResults<Treat> localTreats = realm.where(Treat.class).and()
                .isNotNull(AppStrings.KEY_OBJECT_ID)
                .and()
                .greaterThan(AppStrings.KEY_USER_PURCHASES, AppNumbers.DEFAULT_TREAT_USER_PURCHASED)
                .findAll();
        localTreats = localTreats.sort(AppStrings.KEY_CREATED, Sort.DESCENDING);
        Log.d(TAG, "Purcased Treat list retrieved from db db. details: " + localTreats.toString());
        return localTreats;
    }

    public void insertTreat(Treat newTreat) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                realm.insert(newTreat);
                Log.d(TAG, "treat saved to db. details: " + newTreat.toString());
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    //Todo: catch all possible exceptions instead of the generic 'Exception'
    public void insertOrUpdateTreatsList(Collection<Treat> newTreats) {
        mainThreadRealm = openMainInstance();
        try {
            mainThreadRealm.executeTransactionAsync(realm -> {
                for (Treat newTreat : newTreats) {
                    insertOrUpdateTreatIgnoreUserPurchases(realm, newTreat);
                }
                Log.d(TAG, "Server treats were saved in database. saved list size: " + newTreats.size());
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    public void insertOrUpdateTreatsList(Collection<Treat> newTreats, GenericOperationCallback callback) {
        mainThreadRealm = openMainInstance();
        try {
            mainThreadRealm.executeTransactionAsync(realm -> {
                for (Treat newTreat : newTreats) {
                    insertOrUpdateTreatIgnoreUserPurchases(realm, newTreat);
                }
                callback.onSuccess();
                Log.d(TAG, "Server treats were saved in database. saved list size: " + newTreats.size());
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

    public void syncRealmWithServerTreats(Context context, List<Treat> serverTreats, List<Treat> currentlyPresentedTreats, GenericOperationCallback callback) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm1 -> {
                RealmResults<Treat> localRealmObjTreats = RealmManager.getInstance().getVisibleTreatsAsRealmResults(realm1);
                if (localRealmObjTreats == null || serverTreats == null) {
                    Log.e(AppStrings.REALM_SYNC_SERVER_TAG, "Sync failed. One of the quotes list is null. Server list: " + serverTreats + " local db list: " + localRealmObjTreats);
                    return;
                }
                //Convert serverTreats to hashMap
                Map<String, Treat> serverTreatsMap = convertTreatListToMap(serverTreats);

                for (final Treat localRealmObjTreat : localRealmObjTreats) {
                    String objectId = localRealmObjTreat.getObjectId();
                    Treat match = serverTreatsMap.get(objectId);
                    if (match != null) {
                        // Treat exists in Realm. Remove from server treats map to prevent insert later.
                        serverTreatsMap.remove(objectId);
                        updateIfNeededIgnoreUserPurchases(realm1, match, localRealmObjTreat);
                    } else {
                        // Entry doesn't exist. Remove it from the database if it was never purchased.
                        if (localRealmObjTreat.getUserPurchases() > 0) {
                            if (localRealmObjTreat.isVisible()) {
                                Log.d(TAG, "Treat with text: " + localRealmObjTreat.getText() + " was already purchased " + localRealmObjTreat.getUserPurchases() + " times and won't be deleted from local data base. It would be set invisible in main treats page");
                                localRealmObjTreat.setVisible(false);
                                updateTreatVisibillityColumn(localRealmObjTreat);
                            } else {
                                Log.d(TAG, "Treat with text: " + localRealmObjTreat.getText() + " was already purchased " + localRealmObjTreat.getUserPurchases() + " times and won't be deleted from local data base. It is already invisible in main treats page");
                            }
                        } else {
                            RealmResults<Treat> treatForDelete = realm1.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, localRealmObjTreat.getObjectId()).findAll();
                            treatForDelete.deleteFirstFromRealm();
                            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Treat deleted from db. details: " + localRealmObjTreat.toString());
                        }
                    }
                }
                insertOrUpdateTreatsList(serverTreatsMap.values(), callback);
            }, () -> {
                currentlyPresentedTreats.clear();
                currentlyPresentedTreats.addAll(getVisibleTreats()); //Inhabit upon success
                callback.onSuccess(); //Let networkOperations know
            }, error -> {
                Log.e(TAG, "Sync error for downloaded treas in realm. Details: " + error.getMessage());
                currentlyPresentedTreats.clear();
                currentlyPresentedTreats.addAll(getVisibleTreats()); //Inhabit upon success
                callback.onFailure(context.getResources().getString(R.string.error_no_connection_and_local_db_active));
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    public void syncPurchasedTreatsInDb(Context context, List<Treat> serverTreats, GenericOperationCallback callback) {
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
                        updateTreatUserPurchasesColumn(match); //Only purchases fields are different
                    }
                }
                insertOrUpdateTreatsList(serverTreatsMap.values(), callback);
            }, error -> {
                    Log.e(TAG, "Sync error for downloaded treas in realm. Details: " + error.getMessage());
                    //currentlyPresentedTreats.clear();
                    //currentlyPresentedTreats.addAll(getVisibleTreats()); //Inhabit upon success
                    callback.onFailure(context.getResources().getString(R.string.error_no_connection_and_local_db_active));
                });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    private void updateIfNeededIgnoreUserPurchases(Realm realm, Treat serverTreat, Treat realmObjTreat) {
        Treat finalLocalTreat = realm.copyFromRealm(realmObjTreat);
        if (!serverTreat.equals(finalLocalTreat)) {
            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Scheduling update for treat: " + serverTreat.toString());
            updateLocalTreatIgnoreUserPurchases(realmObjTreat, serverTreat);
        } else {
            Log.d(AppStrings.REALM_SYNC_SERVER_TAG, "Treat equality in databases. no update required for " + serverTreat.toString());
        }
    }

    public void updateTreat(Treat newTreat) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                Treat localTreat = realm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, newTreat.getObjectId()).findFirst();
                updateLocalTreatIgnoreUserPurchases(localTreat, newTreat);
                Log.d(TAG, "Treat updated in db. details: " + newTreat);
                //realm.executeTransactionAsync(realm1 -> realm.insertOrUpdate(newTreat));
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

    private void updateLocalTreatIgnoreUserPurchases(Treat localTreat, Treat newTreat) {
        //localTreat.setObjectId(newTreat.getObjectId()); Primary key - cannot be setted
        //localTreat.setCreated(newTreat.getCreated()); //Constant
        //localTreat.setOwnerId(newTreat.getOwnerId()); //Constant
        localTreat.setBgImageName(newTreat.getBgImageName());
        localTreat.setFontPath(newTreat.getFontPath());
        localTreat.setText(newTreat.getText());
        localTreat.setTextColor(newTreat.getTextColor());
        localTreat.setTextSize(newTreat.getTextSize());
        localTreat.setPurchasesLimit(newTreat.getPurchasesLimit());
        //localTreat.setUserPurchases(newTreat.getUserPurchases()); //Excluded on purpose. check method updateTreatUserPurchasesColumn
        localTreat.setAllPurchases(newTreat.getAllPurchases());
        localTreat.setVisible(newTreat.isVisible());
        Log.d(TAG, "Treat updated in db. details: " + localTreat);
    }

    private void insertOrUpdateTreatIgnoreUserPurchases(Realm realm, Treat newTreat) {
        Treat localTreat = realm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, newTreat.getObjectId()).findFirst();
        if (localTreat == null || localTreat.getObjectId() == null) {
            realm.insert(newTreat);
        } else {
            updateLocalTreatIgnoreUserPurchases(localTreat, newTreat);
        }
    }

    public void updateTreatUserPurchasesColumn(Treat newTreat) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                Treat localTreat = realm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, newTreat.getObjectId()).findFirst();
                assert localTreat != null;
                localTreat.setUserPurchases(newTreat.getUserPurchases());
                Log.d(TAG, "Treat's user purchases updated in db. details: " + localTreat);
                //realm.executeTransactionAsync(realm1 -> realm.insertOrUpdate(newTreat));
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }

    public void updateTreatVisibillityColumn(Treat newTreat) {
        try {
            mainThreadRealm = openMainInstance();
            mainThreadRealm.executeTransactionAsync(realm -> {
                Treat localTreat = realm.where(Treat.class).equalTo(AppStrings.KEY_OBJECT_ID, newTreat.getObjectId()).findFirst();
                assert localTreat != null;
                localTreat.setVisible(newTreat.isVisible());
                Log.d(TAG, "Treat's user purchases updated in db. details: " + localTreat);
                //realm.executeTransactionAsync(realm1 -> realm.insertOrUpdate(newTreat));
            });
        } finally {
            if (mainThreadRealm != null) {
                mainThreadRealm.close();
            }
        }
    }
}


