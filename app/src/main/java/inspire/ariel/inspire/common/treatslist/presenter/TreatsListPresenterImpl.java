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
import com.facebook.CallbackManager;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

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
    @Getter CallbackManager fbCallbackManager;

    private TreatListAdapterPresenter treatListAdapterPresenter;
    private TreatsListView treatsListView;
    private boolean isFetchingMethodUnlocked;
    private int allServerTreatsListSize;
    private Map<String, String> errorsMap;

    public TreatsListPresenterImpl(AppComponent appComponent, TreatsListView view) {
        appComponent.inject(this);
        isFetchingMethodUnlocked = true;
        allServerTreatsListSize = Integer.MAX_VALUE;
        this.treatsListView = view;
    }

    /**
     * Init
     **/

    @Override
    public void init(TreatListAdapterPresenter adapterPresenter) {
        this.treatListAdapterPresenter = adapterPresenter;
        this.treatListAdapterPresenter.setTreats(model.getTreats());
        initErrorsMap();
        fetchInitialTreats(initialTreatsDownloadCallback);
        fetchDataSetSize();
        checkIfUserLoggedIn();
    }

    private void initErrorsMap() {
        errorsMap = new HashMap<>();
        errorsMap.put(AppStrings.BACKENDLESS_ERROR_CODE_INVALID_LOGIN_OR_PASSWORD, customResourcesProvider.getResources().getString(R.string.error_invalid_login_or_password));
        errorsMap.put(AppStrings.BACKENDLESS_ERROR_CODE_EMPTY_PASSWORD_INPUT, customResourcesProvider.getResources().getString(R.string.error_empty_password_input));
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
            model.getTreats().add(0, newTreat);
            treatListAdapterPresenter.notifyDataSetChanged();
            treatsListView.scrollTreatListToTop();
            allServerTreatsListSize++;
            DataManager.getInstance().setMessagesSize(0);
        }
    }

    @Override
    public void onTreatUpdated(Intent data) {
        Treat treat = data.getParcelableExtra(AppStrings.KEY_TREAT);
        initTreatImage(treat);
        int treatPosition = data.getIntExtra(AppStrings.KEY_TREAT_POSITION, AppNumbers.ERROR_INT);
        model.getTreats().set(treatPosition, treat);
        treatListAdapterPresenter.notifyDataSetChanged();
    }

    /**
     * Server Communication
     * Not Thread Safe
     */
    public void fetchPagingTreats(AsyncCallback<List<Treat>> callback) {
        if (isFetchingMethodUnlocked) {
            treatsListView.showProgressDialog(treatsListView.getPagingProgressDialog());
            isFetchingMethodUnlocked = false;
            treatsStorage.find(treatsQueryBuilder, callback);
        }
    }

    public void fetchInitialTreats(AsyncCallback<List<Treat>> callback) {
        try {
            treatsStorage.find(treatsQueryBuilder, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private AsyncCallback<List<Treat>> initialTreatsDownloadCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            isFetchingMethodUnlocked = true;
            if (serverTreats.size() == 0 && model.getTreats().size() == 0) {
                treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_treats));
                return;
            }
            onFullTreatsResponseReceive(serverTreats);
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Treats retrieval error: " + fault.getDetail());
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    private AsyncCallback<List<Treat>> pagingCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            //TODO: implement a small progress treatsListView (like in facebook paging)
            isFetchingMethodUnlocked = true;
            treatsListView.dismissProgressDialog(treatsListView.getPagingProgressDialog());
            if (serverTreats.size() != 0) {
                onFullTreatsResponseReceive(serverTreats);
            }
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Treats retrieval error: " + fault.getDetail());
            isFetchingMethodUnlocked = true;
            treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    private void onFullTreatsResponseReceive(List<Treat> serverTreats) {
        model.getTreats().addAll(serverTreats);
        treatsQueryBuilder.prepareNextPage();
        initTreatsListImages(serverTreats);
        treatListAdapterPresenter.notifyDataSetChanged();
        treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
    }

    @Override
    public void deleteTreat(int treatPosition) {
        treatsStorage.remove(model.getTreats().get(treatPosition), new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                treatsListView.dismissProgressDialog(treatsListView.getMainProgressDialog());
                model.getTreats().remove(treatPosition);
                allServerTreatsListSize--;
                treatListAdapterPresenter.notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_delete_treat));
                Log.e(TAG, "Treat deletion failed. Reason: " + fault.getDetail());
            }
        });
    }

    private void fetchDataSetSize() {

        treatsStorage.getObjectCount(new AsyncCallback<Integer>() {

            @Override
            public void handleResponse(Integer size) {
                Log.i(TAG, "Treats list size in server: " + size);
                TreatsListPresenterImpl.this.allServerTreatsListSize = size;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error getting server treats array size. size stays Integer.MAX_VALUE. error reason: " + fault.getDetail());
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
                        treatsListView.onUserLoggedIn();
                        return;
                    }
                    treatsListView.onUserLoggedOut();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    treatsListView.onUserLoggedOut();
                    Log.e(TAG, "Error in login validation. Reason: " + fault.getDetail() + " making login available");
                }
            });
        } catch (Exception e) {
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
                if (errorsMap.containsKey(fault.getCode())) {
                    treatsListView.onServerOperationFailed(errorsMap.get(fault.getCode()));
                    return;
                }
                treatsListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.generic_error_login));
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
        Uri uri = Uri.parse(AppStrings.PREFIX_DRAWABLE_PATH + treat.getBgImageName()); //TODO: try catch for failures
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
            if (adapterPosition == (model.getTreats().size() - 1) && networkHelper.hasNetworkAccess(treatsListView.getContext())) {
                try {
                    if (model.getTreats().size() < allServerTreatsListSize) {
                        fetchPagingTreats(pagingCallback);
                    }
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

