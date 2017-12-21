package inspire.ariel.inspire.common.treatslist.presenter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.orhanobut.hawk.Hawk;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapterPresenter;
import inspire.ariel.inspire.common.treatslist.model.TreatListModel;
import inspire.ariel.inspire.common.treatslist.view.TreatsListView;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.common.utils.errorutils.ErrorsManager;
import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;
import lombok.Getter;

public class TreatsListPresenterImpl implements TreatsListPresenter {

    private static final String TAG = TreatsListPresenterImpl.class.getName();

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_TREATS)
    IDataStore<Treat> treatsStorage;

    @Inject
    TreatListModel model;

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    DataQueryBuilder allOwnerTreatsQueryBuilder;

    @Inject
    LoadRelationsQueryBuilder<Treat> purchasedTreatsRelationsQueryBuilder;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    ErrorsManager errorsManager;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_USERS)
    IDataStore<BackendlessUser> usersStorage;

    private TreatListAdapterPresenter treatListAdapterPresenter;
    private TreatsListView treatsListView;
    private boolean fetchingMethodUnlocked;
    private boolean pagingEnabled;


    /**
     * Init
     **/

    public TreatsListPresenterImpl(AppComponent appComponent, TreatsListView view) {
        appComponent.inject(this);
        fetchingMethodUnlocked = true;
        this.treatsListView = view;
    }

    @Override
    public void startOperations(TreatListAdapterPresenter adapterPresenter) {
        this.treatListAdapterPresenter = adapterPresenter;
        adapterPresenter.setOnPurchaseClickListener((treat, treatPosition) -> treatsListView.showEnterAdminPasswordDialog(treat, treatPosition));
        Hawk.put(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER, true);
        if (Hawk.get(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER)) {
            Log.d(TAG, "Starting first time logged in methods flow");
            startInitialLogin();
            return;
        }
        startRegularLogin();
    }

    private void initOffline() {
        Log.i(TAG, "Start app offline. Results fetched from Local Data base");
        Log.i(TAG, "Local db treats array size:" + model.getTreatsInAdapter().size());
        initTreatsListImages(model.getTreatsInAdapter());
        treatListAdapterPresenter.setTreats(model.getTreatsInAdapter());
        treatListAdapterPresenter.notifyDataSetChanged();
        pagingEnabled = false;
    }

    private void startInitialLogin() {
        treatsListView.showProgressDialog(treatsListView.getMainProgressDialog());
        setUserDetailsInDataManager();
        model.removeAllTreatsFromDb();
        fetchInitialTreats(firstTimeLoggedInFetchTreatsCallback);
    }

    private void startRegularLogin() {
        if (NetworkHelper.getInstance().hasNetworkAccess(treatsListView.getContext())) {
            initOffline();
            checkIfUserLoggedIn();
        } else {
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
            initOffline();
        }
    }

    /**
     * Lifecycle Methods
     **/

    @Override
    public void onDestroy() {
        treatsListView = null;
    }

    @Override
    public void OnNewIntent(Intent intent) {
        Log.d(TAG, "On new intent is called in " + TAG);
        if (intent.getParcelableExtra(AppStrings.KEY_TREAT) != null) { //If owner created a new quote
            Treat newTreat = intent.getParcelableExtra(AppStrings.KEY_TREAT);
            Log.d(TAG, "New treat entered from push notification" + TAG);
            initTreatImage(newTreat);
            model.getTreatsInAdapter().add(0, newTreat);
            model.saveTreatToDb(newTreat);
            treatListAdapterPresenter.notifyDataSetChanged();
            treatsListView.scrollTreatListToTop();
            DataManager.getInstance().setMessagesSize(0);
            String messageForUser = intent.getStringExtra(AppStrings.KEY_MESSAGE_FOR_DISPLAY);
            if (messageForUser != null) { //Happens when a treat is sent and TreatsListActivity is in foreground
                treatsListView.showToastMessage(messageForUser);
            }
        }
    }

    @Override
    public void onTreatUpdated(Intent data) {
        Treat treat = data.getParcelableExtra(AppStrings.KEY_TREAT);
        initTreatImage(treat);
        int treatPosition = data.getIntExtra(AppStrings.KEY_TREAT_POSITION, AppNumbers.ERROR_INT);
        model.getTreatsInAdapter().set(treatPosition, treat);
        model.updateTreatInDb(treat);
        treatListAdapterPresenter.notifyDataSetChanged();
    }

    /**
     * Server Communication
     * Not Thread Safe
     */

    private void fetchPurchasedTreats(List<Treat> fullRawPurchasedTreatList) {
        usersStorage.loadRelations(DataManager.getInstance().getUser().getObjectId(), purchasedTreatsRelationsQueryBuilder, new AsyncCallback<List<Treat>>() {
            @Override
            public void handleResponse(List<Treat> purchasedServerTreats) {
                Log.d(TAG, "Purchased treats fetched. data: " + purchasedServerTreats.toString());
                if (!purchasedServerTreats.isEmpty()) {
                    fullRawPurchasedTreatList.addAll(purchasedServerTreats);
                    purchasedTreatsRelationsQueryBuilder.prepareNextPage();
                    fetchPurchasedTreats(fullRawPurchasedTreatList);
                }

                if(!purchasedServerTreats.isEmpty()) { //Checking twice if it is empty because of recursive call
                    List<Treat> updatedTreats = convertDuplicatedTreatsListToUpdatedTimesPurchasedUniqueValuesList(fullRawPurchasedTreatList);
                    model.updatePurchasedTreatsInDb(updatedTreats);
                }
                model.setTreatsInAdapter(model.getTreatsFromDb());
                treatListAdapterPresenter.setTreats(model.getTreatsInAdapter()); //Todo: this call should not be made
                initTreatsListImages(model.getTreatsInAdapter());
                treatListAdapterPresenter.notifyDataSetChanged();
                Log.d(TAG, "Purchased treats rray is empty User hasn't purchased any treats. user details: " + DataManager.getInstance().getUser().toString());
                treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
                pagingEnabled = true;
                fetchingMethodUnlocked = true;
                Hawk.put(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER, false);
                Log.i(TAG, "first time log in is done for user:" + DataManager.getInstance().getUser().toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "First time login purchased treats retrieval error: " + fault.getDetail());
                DataManager.getInstance().setUnauthorizedUser();
                initTreatsListImages(model.getTreatsInAdapter()); //Starting at offline mode, fetch images if any
                treatsListView.onServerOperationFailed(errorsManager.getErrorFromFaultCode(fault.getCode(), customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
            }
        });
    }

    private List<Treat> convertDuplicatedTreatsListToUpdatedTimesPurchasedUniqueValuesList(List<Treat> treats) {
        Map<String, Treat> mapWithFreqs = new HashMap<>();
        for (Treat treat : treats) {
            if (!mapWithFreqs.containsKey(treat.getObjectId())) {
                treat.setTimesPurchased(Collections.frequency(treats, treat));
                mapWithFreqs.put(treat.getObjectId(), treat);
            }
        }
        List<Treat> updatedArr = new ArrayList<>();
        updatedArr.addAll(mapWithFreqs.values());
        return updatedArr;
    }

    public void fetchInitialTreats(AsyncCallback<List<Treat>> callback) {
        try {
            treatsStorage.find(allOwnerTreatsQueryBuilder, callback);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
        }
    }

    public void fetchPagingTreats(AsyncCallback<List<Treat>> callback) {
        if (pagingEnabled && fetchingMethodUnlocked) { //Could have been done if one boolean, but created 2 for different purposes
            treatsListView.showProgressDialog(treatsListView.getPagingProgressDialog());
            fetchingMethodUnlocked = false;
            treatsStorage.find(allOwnerTreatsQueryBuilder, callback);
        }
    }

    private AsyncCallback<List<Treat>> firstTimeLoggedInFetchTreatsCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            Log.d(TAG, "First time logged in treats received from server. size: " + serverTreats.size());
            if (serverTreats.isEmpty()) {
                treatsListView.showSnackbarMessage(customResourcesProvider.getResources().getString(R.string.error_no_treats));
                fetchPurchasedTreats(new ArrayList<>());
                return;
            }
            model.saveTreatsListToDb(serverTreats); //TODO: Improve local data base algorithm so deletion would not be required
            allOwnerTreatsQueryBuilder.prepareNextPage();
            fetchPurchasedTreats(new ArrayList<>());
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "First time login treats retrieval error: " + fault.getDetail());
            treatsListView.onServerOperationFailed(errorsManager.getErrorFromFaultCode(fault.getCode(), customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private AsyncCallback<List<Treat>> initialTreatsDownloadCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            try {
                fetchingMethodUnlocked = true;
                Log.d(TAG, "Treats received from server. size: " + serverTreats.size());
                if (serverTreats.isEmpty() && model.getTreatsInAdapter().isEmpty()) {
                    treatsListView.showSnackbarMessage(customResourcesProvider.getResources().getString(R.string.error_no_treats));
                    return;
                }
                model.syncRealmWithServerTreats(serverTreats); //TODO: Improve local data base algorithm so deletion would not be required
                model.setTreatsInAdapter(serverTreats);
                treatListAdapterPresenter.setTreats(model.getTreatsInAdapter()); //Todo: this call is redundant
                pagingEnabled = true;
                onFullTreatsResponseReceive(serverTreats);
            } finally {
                treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
            }
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Treats retrieval error: " + fault.getDetail());
            treatsListView.onServerOperationFailed(errorsManager.getErrorFromFaultCode(fault.getCode(), customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
        }
    };

    private AsyncCallback<List<Treat>> pagingCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            //TODO: implement a small progress treatsListView (like in facebook paging)
            fetchingMethodUnlocked = true;
            treatsListView.dismissProgressDialog(treatsListView.getPagingProgressDialog());
            if (serverTreats.size() != 0) {
                model.getTreatsInAdapter().addAll(serverTreats);
                model.saveTreatsListToDb(serverTreats);
                onFullTreatsResponseReceive(serverTreats);
            } else {
                treatsListView.showSnackbarMessage(customResourcesProvider.getResources().getString(R.string.no_new_quotes_from_paging));
                pagingEnabled = false;
            }
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Treats retrieval error: " + fault.getDetail());
            fetchingMethodUnlocked = true;
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    private void onFullTreatsResponseReceive(List<Treat> serverTreats) {
        allOwnerTreatsQueryBuilder.prepareNextPage();
        initTreatsListImages(serverTreats);
        treatListAdapterPresenter.notifyDataSetChanged();
        treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
    }

    @Override
    public void deleteTreat(int treatPosition) {
        treatsStorage.remove(model.getTreatsInAdapter().get(treatPosition), new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
                model.deleteTreatFromDb(model.getTreatsInAdapter().get(treatPosition));
                model.getTreatsInAdapter().remove(treatPosition);
                treatListAdapterPresenter.notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_delete_treat));
                Log.e(TAG, "Treat deletion failed. Reason: " + fault.getDetail());
            }
        });
    }

    private void checkIfUserLoggedIn() {
        try {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {
                    Log.i(TAG, "Login status check Successful. Is user logged in? " + response);
                    if (response && Hawk.contains(AppStrings.KEY_LOGGED_IN_USER)) {
                        fetchInitialTreats(initialTreatsDownloadCallback);
                        setUserDetailsInDataManager();
                        treatsListView.onUserLoggedIn();
                        return;
                    }
                    treatsListView.onUserLoggedOut();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    treatsListView.onServerOperationFailed(errorsManager.getErrorFromFaultCode(fault.getCode(), customResourcesProvider.getResources().getString(R.string.error_login_validation)));
                    Log.e(TAG, "Error in login validation. Reason: " + fault.getDetail() + " making login available");
                }
            });
        } catch (Exception e) {
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
            e.printStackTrace();
        }
    }

    @Override
    public void logout() {
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if (response) {
                    Backendless.UserService.logout(new AsyncCallback<Void>() {
                        public void handleResponse(Void response) {
                            if (Hawk.contains(AppStrings.KEY_LOGGED_IN_USER)) {
                                Hawk.delete(AppStrings.KEY_LOGGED_IN_USER);
                            }
                            DataManager.getInstance().setUser(new BackendlessUser());
                            treatsListView.onUserLoggedOut();
                        }

                        public void handleFault(BackendlessFault fault) {
                            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_logout));
                            Log.e(TAG, "Logout failed. reason: " + fault.getDetail());
                        }
                    });
                } else {
                    Log.i(TAG, "User was already logged out from server. operation auto-succeeds");
                    treatsListView.onUserLoggedOut();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_logout));
                Log.e(TAG, "Logout failed. reason: " + fault.getDetail());
            }
        });
    }

    @Override
    public void purchaseTreat(CharSequence adminPassword, Treat treat, int treatPosition) {
        if (adminPassword.equals(AppStrings.VAL_PURCHASER)) {
            treatsListView.showProgressDialog(treatsListView.getMainProgressDialog());
            setUserTreatPurchaseRelation(DataManager.getInstance().getUser(), new ArrayList<Treat>() {{
                add(treat);
            }}, treatPosition);
        }
    }

    private void setUserTreatPurchaseRelation(BackendlessUser user, List<Treat> singleTreatInsideList, int treatPosition) {
        usersStorage.addRelation(user, AppStrings.BACKENDLESS_TABLE_USER_COLUMN_PURCHASED_TREATS, singleTreatInsideList,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        model.getTreatsInAdapter().get(treatPosition).setTimesPurchased(singleTreatInsideList.get(0).getTimesPurchased() + 1);
                        model.updateTreatInDb(singleTreatInsideList.get(0));
                        treatListAdapterPresenter.notifyDataSetChanged();
                        Log.i(TAG, "Purchase relation is set for treat: " + singleTreatInsideList.get(0).getText() + " and user: " + String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Server error. Purchase relation was not created: " + fault.getDetail());
                        treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_purchase_treat));
                        //treatDesignerViewController.dismissProgressDialogAndShowErrorMessage(treatDesignerViewController.getResources().getString(R.string.error_treat_leader_relation_creation));
                    }
                });
    }

    /**
     * Data Manager
     */

    private void setUserDetailsInDataManager() {
        BackendlessUser user = Hawk.get(AppStrings.KEY_LOGGED_IN_USER);
        if (user.getObjectId().equalsIgnoreCase(AppStrings.BACKENDLESS_VAL_OWNER_ID)) {
            treatsListView.onAdminLoggedIn();
            DataManager.getInstance().setAdminUser(user);
            return;
        }
        DataManager.getInstance().setNormalUser(user);
    }

    /**
     * Image handling
     */

    private void initTreatsListImages(List<Treat> treats) {
        for (Treat treat : treats) {
            initTreatImage(treat);
        }
    }

    private void initTreatImage(Treat treat) {
        Uri uri = Uri.parse(AppStrings.PREFIX_DRAWABLE_PATH + treat.getBgImageName()); //TODO: Maybe try catch for failures
        treat.setImage(ImageUtils.createDrawableFromUri(uri, treatsListView.getContentResolver(), customResourcesProvider.getResources()));
    }

    /**
     * Scrolling Methods
     **/

    @Getter
    private DiscreteScrollView.ScrollStateChangeListener<?> onScrollChangedListener = new DiscreteScrollView.ScrollStateChangeListener() {
        @Override
        public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

        }

        @Override
        public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
            if (adapterPosition == (model.getTreatsInAdapter().size() - 1) && networkHelper.hasNetworkAccess(treatsListView.getContext())) {
                try {
                    fetchPagingTreats(pagingCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

        }
    };
}

