package inspire.ariel.inspire.common.treatslist.presenter;

import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.utils.asyncutils.CodependentTasksManager;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import lombok.Data;

@Data
class TreatListPresenterNetworkOperations {

    private static final String TAG = TreatListPresenterNetworkOperations.class.getName();
    private TreatsListPresenterImpl presenter;

    public TreatListPresenterNetworkOperations(TreatsListPresenterImpl presenter) {
        this.presenter = presenter;
    }

    void fetchPurchasedTreats(List<Treat> fullRawPurchasedTreatList) {
        presenter.usersStorage.loadRelations(DataManager.getInstance().getUser().getObjectId(), presenter.purchasedTreatsRelationsQueryBuilder, new AsyncCallback<List<Treat>>() {
            @Override
            public void handleResponse(List<Treat> purchasedServerTreats) {
                Log.d(TAG, "Purchased treats fetched. data: " + purchasedServerTreats.toString());
                if (!purchasedServerTreats.isEmpty()) {
                    fullRawPurchasedTreatList.addAll(purchasedServerTreats);
                    presenter.purchasedTreatsRelationsQueryBuilder.prepareNextPage();
                    fetchPurchasedTreats(fullRawPurchasedTreatList);
                }

                if (!purchasedServerTreats.isEmpty()) { //Checking twice if it is empty because of recursive call
                    List<Treat> updatedTreats = presenter.convertDuplicatedTreatsListToUpdatedTimesPurchasedUniqueValuesList(fullRawPurchasedTreatList);
                    presenter.model.updatePurchasedTreatsInDb(updatedTreats);
                }
                presenter.model.setTreatsInAdapter(presenter.model.getVisibleTreatsFromDb());
                presenter.treatListAdapterPresenter.setTreats(presenter.model.getTreatsInAdapter()); //Todo: this call should not be made
                presenter.initTreatsListImages(presenter.model.getTreatsInAdapter());
                presenter.treatListAdapterPresenter.notifyDataSetChanged();
                Log.d(TAG, "Purchased treats array is empty User hasn't purchased any treats. user details: " + DataManager.getInstance().getUser().toString());
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                presenter.pagingEnabled = true;
                presenter.fetchingMethodUnlocked = true;
                Hawk.put(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER, false);
                Log.i(TAG, "first time log in is done for user:" + DataManager.getInstance().getUser().toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "First time login purchased treats retrieval error: " + fault.getDetail());
                DataManager.getInstance().setUnauthorizedUserStatus();
                presenter.initTreatsListImages(presenter.model.getTreatsInAdapter()); //Starting at offline mode, fetch images if any
                presenter.treatsListView.onServerOperationFailed(presenter.errorsManager.getErrorFromFaultCode(fault.getCode(), presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
            }
        });
    }

    AsyncCallback<List<Treat>> firstTimeLoggedInFetchTreatsCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            Log.d(TAG, "First time logged in treats received from server. size: " + serverTreats.size());
            if (serverTreats.isEmpty()) {
                presenter.treatsListView.showSnackbarMessage(presenter.customResourcesProvider.getResources().getString(R.string.error_no_treats));
                fetchPurchasedTreats(new ArrayList<>());
                return;
            }
            presenter.model.saveTreatsListToDb(serverTreats); //TODO: Improve local data base algorithm so deletion would not be required
            presenter.allOwnerTreatsQueryBuilder.prepareNextPage();
            fetchPurchasedTreats(new ArrayList<>());
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "First time login treats retrieval error: " + fault.getDetail());
            presenter.treatsListView.onServerOperationFailed(presenter.errorsManager.getErrorFromFaultCode(fault.getCode(), presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
        }
    };


    @SuppressWarnings("FieldCanBeLocal")
    private AsyncCallback<List<Treat>> initialTreatsDownloadCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            try {
                presenter.fetchingMethodUnlocked = true;
                Log.d(TAG, "Treats received from initial call to server. size: " + serverTreats.size());
                if (serverTreats.isEmpty() && presenter.model.getTreatsInAdapter().isEmpty()) {
                    presenter.treatsListView.showSnackbarMessage(presenter.customResourcesProvider.getResources().getString(R.string.error_no_treats));
                    return;
                }
                presenter.model.setTreatsInAdapter(serverTreats);
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                presenter.model.syncRealmWithServerTreats(serverTreats); //TODO: Improve local data base algorithm so deletion would not be required
                presenter.treatListAdapterPresenter.setTreats(presenter.model.getTreatsInAdapter()); //Todo: this call is redundant
                presenter.pagingEnabled = true;
                presenter.onFullTreatsResponseReceive(serverTreats);
            } finally {
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
            }
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Treats retrieval error: " + fault.getDetail());
            presenter.treatsListView.onServerOperationFailed(presenter.errorsManager.getErrorFromFaultCode(fault.getCode(), presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
        }
    };

    private AsyncCallback<List<Treat>> pagingCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> serverTreats) {
            //TODO: implement a small progress treatsListView (like in facebook paging)
            presenter.fetchingMethodUnlocked = true;
            presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getPagingProgressDialog());
            if (serverTreats.size() != 0) {
                presenter.model.getTreatsInAdapter().addAll(serverTreats);
                presenter.model.saveTreatsListToDb(serverTreats);
                presenter.onFullTreatsResponseReceive(serverTreats);
            } else {
                presenter.treatsListView.showSnackbarMessage(presenter.customResourcesProvider.getResources().getString(R.string.no_new_quotes_from_paging));
                presenter.pagingEnabled = false;
            }
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Treats retrieval error: " + fault.getDetail());
            presenter.fetchingMethodUnlocked = true;
            presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    /**
     * Treat deletion
     */

     void deleteTreatIfPossible(int treatPosition){
        checkTreatAllPurchasesColumnInServer(treatPosition);
    }

    private void checkTreatAllPurchasesColumnInServer(int treatPosition){
        Treat localTreat = presenter.model.getTreatsInAdapter().get(treatPosition);
        presenter.treatsStorage.findById(localTreat.getObjectId(), new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat serverTreat) {
                if(serverTreat.getAllPurchases() == 0){
                    deleteTreatFromServer(localTreat, treatPosition);
                }else{
                    setTreatInvisible(treatPosition);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_delete_treat));
                Log.e(TAG, "Treat deletion failed. Reason: " + fault.getDetail());
            }
        });
    }

    /**
     * Set treat invisible to users
     * Users who already purchased it may review it in the purchases history
     */
    void setTreatInvisible(int treatPosition) {
        Treat localTreat = presenter.model.getTreatsInAdapter().get(treatPosition);
        localTreat.setVisible(false);
        localTreat.setPurchaseable(false);
        presenter.treatsStorage.save(localTreat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat response) {
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                presenter.model.updateTreatInDb(localTreat);
                presenter.model.getTreatsInAdapter().remove(treatPosition);
                presenter.treatListAdapterPresenter.notifyDataSetChanged();
                presenter.treatsListView.showSnackbarMessage(presenter.customResourcesProvider.getResources().getString(R.string.treat_became_invisible));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                localTreat.setPurchaseable(true);
                presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_delete_treat));
                Log.e(TAG, "Treat update failed. Reason: " + fault.getDetail());
            }
        });
    }

    private void deleteTreatFromServer(Treat treat, int treatPosition){
        presenter.treatsStorage.remove(treat, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                presenter.model.deleteTreatFromDb(treat);
                presenter.model.getTreatsInAdapter().remove(treatPosition);
                presenter.treatListAdapterPresenter.notifyDataSetChanged();
                presenter.treatsListView.showSnackbarMessage(presenter.customResourcesProvider.getResources().getString(R.string.delete_successful));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_delete_treat));
                Log.e(TAG, "Treat deletion failed. Reason: " + fault.getDetail());
            }
        });
    }

    /**
     * Logout and check if logged in
     */
    void checkIfUserLoggedIn() {
        try {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {
                    Log.i(TAG, "Login status check Successful. Is user logged in? " + response);
                    BackendlessUser user = Hawk.get(AppStrings.KEY_LOGGED_IN_USER);
                    if (response && user.getObjectId() != null) {
                        fetchInitialTreats(initialTreatsDownloadCallback);
                        presenter.setUserDetailsInDataManager();
                        presenter.treatsListView.onUserLoggedIn();
                        return;
                    }
                    presenter.treatsListView.onUserLoggedOut();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    presenter.treatsListView.onServerOperationFailed(presenter.errorsManager.getErrorFromFaultCode(fault.getCode(), presenter.customResourcesProvider.getResources().getString(R.string.error_login_validation)));
                    Log.e(TAG, "Error in login validation. Reason: " + fault.getDetail() + " making login available");
                }
            });
        } catch (Exception e) {
            presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
            e.printStackTrace();
        }
    }

    void logout() {
        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if (response) {
                    Backendless.UserService.logout(new AsyncCallback<Void>() {
                        public void handleResponse(Void response) {
                            presenter.treatsListView.onUserLoggedOut();
                        }

                        public void handleFault(BackendlessFault fault) {
                            presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_logout));
                            Log.e(TAG, "Logout failed. reason: " + fault.getDetail());
                        }
                    });
                } else {
                    Log.i(TAG, "User was already logged out from server. operation auto-succeeds");
                    presenter.treatsListView.onUserLoggedOut();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_logout));
                Log.e(TAG, "Logout failed. reason: " + fault.getDetail());
            }
        });
    }

    /**
     * Purchase treat
     */

    void purchaseTreat(BackendlessUser user, List<Treat> singleTreatInsideList, int treatPosition) {
        CodependentTasksManager tasksManager = new CodependentTasksManager(new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                Treat newTreat = singleTreatInsideList.get(0);
                presenter.model.getTreatsInAdapter().get(treatPosition).setUserPurchases(newTreat.getUserPurchases() + 1); //Local update
                presenter.model.getTreatsInAdapter().get(treatPosition).setAllPurchases(newTreat.getUserPurchases() + 1); //Local update
                presenter.model.updateTreatInDb(newTreat);
                presenter.treatListAdapterPresenter.notifyDataSetChanged();
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                Log.i(TAG, "Treat with text: " + singleTreatInsideList.get(0).getText() + " was successfully purchased for user: " + String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
            }

            @Override
            public void onFailure(String errorForUser) {
                presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_purchase_treat));
            }
        }, AppNumbers.MUST_COMPLETED_TASKS_ON_TREAT_PURCHASES);

        updateTreatTimesPurchased(singleTreatInsideList.get(0), tasksManager);
        setRelationBetweenUserAndPurchasedTreat(user, singleTreatInsideList, tasksManager);

    }

    private void updateTreatTimesPurchased(Treat newTreat, CodependentTasksManager tasksManager){
        newTreat.setAllPurchases(newTreat.getAllPurchases() + 1);
        presenter.treatsStorage.save(newTreat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat response) {
                Log.i(TAG, "Successfully updated alPurchases to " + newTreat.getAllPurchases() + " For treat with id: " + newTreat.getObjectId());
                tasksManager.onSingleOperationSuccessful();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                tasksManager.onSingleOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_purchase_treat));
                Log.e(TAG, "Could not update alPurchases. reason: " + fault.getDetail());
            }
        });
    }

    private void setRelationBetweenUserAndPurchasedTreat(BackendlessUser user, List<Treat> singleTreatInsideList, CodependentTasksManager tasksManager){
        presenter.usersStorage.addRelation(user, AppStrings.BACKENDLESS_TABLE_USER_COLUMN_PURCHASED_TREATS, singleTreatInsideList,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        tasksManager.onSingleOperationSuccessful();
                        Log.i(TAG, "Purchase relation is set for treat: " + singleTreatInsideList.get(0).getText() + " and user: " + String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Server error. Purchase relation was not created: " + fault.getDetail());
                        presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_purchase_treat));
                    }
                });
    }

    /**
     * Fetch treats
     * callbacks are in presenter
     */


    public void fetchInitialTreats(AsyncCallback<List<Treat>> callback) {
        try {
            presenter.treatsStorage.find(presenter.allOwnerTreatsQueryBuilder, callback);
        } catch (Exception e) {
            e.printStackTrace();
            presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
        }
    }

    public void fetchPagingTreats(AsyncCallback<List<Treat>> callback) {
        if (presenter.pagingEnabled && presenter.fetchingMethodUnlocked) { //Could have been done if one boolean, but created 2 for different purposes
            presenter.treatsListView.showProgressDialog(presenter.treatsListView.getPagingProgressDialog());
            presenter.fetchingMethodUnlocked = false;
            presenter.treatsStorage.find(presenter.allOwnerTreatsQueryBuilder, callback);
        }
    }
}
