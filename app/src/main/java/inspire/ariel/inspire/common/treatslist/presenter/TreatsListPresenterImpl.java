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
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

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
    DataQueryBuilder treatsQueryBuilder;

    @Inject
    NetworkHelper networkHelper;

    @Inject
    ErrorsManager errorsManager;

    private TreatListAdapterPresenter treatListAdapterPresenter;
    private TreatsListView treatsListView;
    private boolean fetchingMethodUnlocked;
    private boolean pagingEnabled;

    public TreatsListPresenterImpl(AppComponent appComponent, TreatsListView view) {
        appComponent.inject(this);
        fetchingMethodUnlocked = true;
        this.treatsListView = view;
    }

    /**
     * Init
     **/

    @Override
    public void startOperations(TreatListAdapterPresenter adapterPresenter) {
        this.treatListAdapterPresenter = adapterPresenter;
        if (NetworkHelper.getInstance().hasNetworkAccess(treatsListView.getContext())) {
            initOffline();
            checkIfUserLoggedIn();
        } else {
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
            initOffline();
        }
    }

    private void initOffline() {
        Log.i(TAG, "Start app offline. Results fetched from Local Data base");
        Log.i(TAG, "Local db treats array size:" + model.getTreatsInAdapter().size());
        initTreatsListImages(model.getTreatsInAdapter());
        treatListAdapterPresenter.setTreats(model.getTreatsInAdapter());
        treatListAdapterPresenter.notifyDataSetChanged();
        pagingEnabled = false;
        treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
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
        if (intent.getParcelableExtra(AppStrings.KEY_TREAT) != null) { //If owner created a new quote
            Treat newTreat = intent.getParcelableExtra(AppStrings.KEY_TREAT);
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

    public void fetchInitialTreats(AsyncCallback<List<Treat>> callback) {
        try {
            treatsStorage.find(treatsQueryBuilder, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fetchPagingTreats(AsyncCallback<List<Treat>> callback) {
        if (pagingEnabled && fetchingMethodUnlocked) { //Could have been done if one boolean, but created 2 for different purposes
            treatsListView.showProgressDialog(treatsListView.getPagingProgressDialog());
            fetchingMethodUnlocked = false;
            treatsStorage.find(treatsQueryBuilder, callback);
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private AsyncCallback<List<Treat>> initialTreatsDownloadCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            fetchingMethodUnlocked = true;
            if (serverTreats.size() == 0 && model.getTreatsInAdapter().size() == 0) {
                treatsListView.showSnackbarMessage(customResourcesProvider.getResources().getString(R.string.error_no_treats));
                return;
            }
            model.syncRealmWithServerTreats(serverTreats); //TODO: Improve local data base algorithm so deletion would not be required
            model.setTreatsInAdapter(serverTreats);
            treatListAdapterPresenter.setTreats(model.getTreatsInAdapter());
            pagingEnabled = true;
            onFullTreatsResponseReceive(serverTreats);
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
            }else{
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
        treatsQueryBuilder.prepareNextPage();
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
                model.removeTreatFromDb(model.getTreatsInAdapter().get(treatPosition));
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
                    if (response) {
                        fetchInitialTreats(initialTreatsDownloadCallback);
                        treatsListView.onUserLoggedIn();
                        return;
                    }
                    treatsListView.onUserLoggedOut();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    treatsListView.onServerOperationFailed(errorsManager.getErrorFromFaultCode("999", customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
                    Log.e(TAG, "Error in login validation. Reason: " + fault.getDetail() + " making login available");
                }
            });
        } catch (Exception e) {
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
            e.printStackTrace();
        }
    }

    @Override
    public void login(CharSequence password) {
        Backendless.UserService.login("james.bond@mi6.co.uk", String.valueOf(password), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {
                treatsListView.onUserLoggedIn();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Login failed. reason: " + fault.getDetail());
                String defaultErrorForUser = customResourcesProvider.getResources().getString(R.string.generic_error_login);
                String finalErrorForUser = errorsManager.getErrorFromFaultCode(fault.getCode(), defaultErrorForUser);
                treatsListView.onServerOperationFailed(finalErrorForUser);
            }
        }, true);
    }

    @Override
    public void logout() {
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if (response) {
                    Backendless.UserService.logout(new AsyncCallback<Void>() {
                        public void handleResponse(Void response) {
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

