package inspire.ariel.inspire.common.treatslist.presenter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
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
import inspire.ariel.inspire.common.utils.backendutils.NetworkChecker;
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
    NetworkChecker networkChecker;

    @Inject
    ErrorsManager errorsManager;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_USERS)
    IDataStore<BackendlessUser> usersStorage;

    TreatListPresenterNetworkOperations networkOperations;
    TreatListAdapterPresenter treatListAdapterPresenter;
    TreatsListView treatsListView;
    boolean fetchingMethodUnlocked;
    boolean pagingEnabled;


    /**
     * Init
     **/
    public TreatsListPresenterImpl(AppComponent appComponent, TreatsListView view) {
        appComponent.inject(this);
        fetchingMethodUnlocked = true;
        this.treatsListView = view;
        networkOperations = new TreatListPresenterNetworkOperations(this); //Not injected, but package private
    }

    @Override
    public void prepareForFirsLaunchIfNeeded() {
        if (!Hawk.contains(AppStrings.KEY_IS_FIRST_LAUNCH) || (boolean) Hawk.get(AppStrings.KEY_IS_FIRST_LAUNCH)) {
            treatsListView.showProgressDialog(treatsListView.getMainProgressDialog());
        }
    }

    @Override
    public void startOperations(TreatListAdapterPresenter adapterPresenter) {
        this.treatListAdapterPresenter = adapterPresenter;
        adapterPresenter.setOnPurchaseClickListener((treat, treatPosition) -> treatsListView.showEnterAdminPasswordDialog(treat, treatPosition));
        if (Hawk.get(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER)) {
            Log.i(TAG, "User has logged in for the first time. Start first time logged in methods flow");
            startAfterInitialLoginOperations();
            return;
        }
        Log.d(TAG, "User hasn't logged in for the first time. Start the usual method flow");
        startNotFirstTimeLoginOperations();
    }

    private void initOffline() {
        Log.i(TAG, "Start app offline. Results fetched from Local Data base");
        Log.i(TAG, "Local db treats array size:" + model.getTreatsInAdapter().size());
        treatListAdapterPresenter.setTreats(model.getTreatsInAdapter());
        pagingEnabled = false;
        if (!model.getTreatsInAdapter().isEmpty()) {
            initTreatsListImages(model.getTreatsInAdapter());
            treatListAdapterPresenter.notifyDataSetChanged();
        } else {
            treatsListView.showProgressDialog(treatsListView.getMainProgressDialog());
        }
    }

    private void startAfterInitialLoginOperations() {
        if (NetworkChecker.getInstance().hasNetworkAccess(treatsListView.getContext())) {
            treatsListView.showProgressDialog(treatsListView.getMainProgressDialog());
            setUserDetailsInDataManager();
            model.deleteAllTreatsFromDb();
            //No need to check if user is logged in. just fetch the treats.
            networkOperations.fetchInitialTreats(networkOperations.firstTimeLoggedInFetchTreatsCallback);
        } else {
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection_for_first_time_logged_in));
        }
    }

    private void startNotFirstTimeLoginOperations() {
        if (NetworkChecker.getInstance().hasNetworkAccess(treatsListView.getContext())) {
            initOffline();
            networkOperations.checkIfUserLoggedIn();
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
     * Server communication interface methods
     */

    @Override
    public void setTreatUnPurchaseable(int treatPosition) {
        networkOperations.deleteTreatIfPossible(treatPosition);
    }

    @Override
    public void logout() {
        networkOperations.logout();
    }

    @Override
    public void purchaseTreat(String adminPassword, Treat treat, int treatPosition) {
        if (adminPassword.equalsIgnoreCase(AppStrings.VAL_PURCHASER)) {
            networkOperations.purchaseTreat(DataManager.getInstance().getUser(), new ArrayList<Treat>() {{
                add(treat);
            }}, treatPosition);
        } else {
            treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
            treatsListView.showSnackbarMessage(customResourcesProvider.getResources().getString(R.string.error_invalid_password));
        }
    }


    /**
     * Downloaded treats results support methods
     */
    List<Treat> convertDuplicatedTreatsListToUpdatedTimesPurchasedUniqueValuesList(List<Treat> treats) {
        Map<String, Treat> mapWithFreqs = new HashMap<>();
        for (Treat treat : treats) {
            if (!mapWithFreqs.containsKey(treat.getObjectId())) {
                treat.setUserPurchases(Collections.frequency(treats, treat));
                mapWithFreqs.put(treat.getObjectId(), treat);
            }
        }
        List<Treat> updatedArr = new ArrayList<>();
        updatedArr.addAll(mapWithFreqs.values());
        return updatedArr;
    }

    void onFullTreatsResponseReceive(List<Treat> serverTreats) {
        allOwnerTreatsQueryBuilder.prepareNextPage();
        initTreatsListImages(serverTreats);
        treatListAdapterPresenter.notifyDataSetChanged();
        treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
    }

    /**
     * Data Manager
     */

    void setUserDetailsInDataManager() {
        //User must be with valid id in Hawk at this point
        BackendlessUser user = Hawk.get(AppStrings.KEY_LOGGED_IN_USER);
        if (user.getObjectId().equalsIgnoreCase(AppStrings.BACKENDLESS_VAL_OWNER_ID)) {
            DataManager.getInstance().setAdminUserStatus(user);
            Log.i(TAG, "Admin with id " + user.getObjectId() + " has logged in");
            return;
        }
        Log.i(TAG, "Regular with id " + user.getObjectId() + " has logged in");
        DataManager.getInstance().setNormalUserStatus(user);
    }

    /**
     * Image handling
     */

    void initTreatsListImages(List<Treat> treats) {
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
            if (adapterPosition == (model.getTreatsInAdapter().size() - 1) && networkChecker.hasNetworkAccess(treatsListView.getContext())) {
                try {
                    networkOperations.fetchPagingTreats(networkOperations.getPagingCallback());
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
