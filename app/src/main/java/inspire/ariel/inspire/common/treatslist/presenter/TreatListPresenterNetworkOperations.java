package inspire.ariel.inspire.common.treatslist.presenter;

import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.utils.asyncutils.CodependentTasksManager;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import lombok.Data;

@Data
@DebugLog
class TreatListPresenterNetworkOperations {

    private static final String TAG = TreatListPresenterNetworkOperations.class.getName();
    private TreatsListPresenterImpl presenter;

    public TreatListPresenterNetworkOperations(TreatsListPresenterImpl presenter) {
        this.presenter = presenter;
    }

    void fetchPurchasedTreats(List<Treat> purchasedTreats) {
        presenter.usersStorage.loadRelations(DataManager.getInstance().getUser().getObjectId(), presenter.purchasedTreatsRelationsQueryBuilder, new AsyncCallback<List<Treat>>() {
            @Override
            public void handleResponse(List<Treat> purchasedServerTreats) {
                Log.d(TAG, "Purchased treats fetched. data: " + purchasedServerTreats.toString());
                if (!purchasedServerTreats.isEmpty()) {
                    purchasedTreats.addAll(purchasedServerTreats);
                    presenter.purchasedTreatsRelationsQueryBuilder.prepareNextPage();
                    fetchPurchasedTreats(purchasedTreats);
                }

                Log.d(TAG, "User purchsed treats array size: " + purchasedServerTreats.size());
                if (!purchasedServerTreats.isEmpty()) { //Checking twice if it is empty because of recursive call
                    presenter.increaseAllUserPurchasesByOne(purchasedTreats);
                    presenter.model.syncPurchasedTreatsInDb(presenter.treatsListView.getContext(), purchasedTreats, new GenericOperationCallback() {
                        @Override
                        public void onSuccess() {
                            presenter.treatListAdapterPresenter.setTreats(presenter.model.getTreats()); //Data set pointer was changed in model
                            presenter.initTreatsListImages(presenter.model.getTreats());
                            presenter.treatListAdapterPresenter.notifyDataSetChanged();
                            presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                            presenter.pagingEnabled = true;
                            presenter.fetchingMethodUnlocked = true;
                            Hawk.put(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER, false);
                            Log.i(TAG, "first time log in is done for user:" + DataManager.getInstance().getUser().toString());
                        }

                        @Override
                        public void onFailure(String errorForUser) {

                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "First time login purchased treats retrieval error: " + fault.getDetail());
                DataManager.getInstance().setUnauthorizedUserStatus();
                presenter.initTreatsListImages(presenter.model.getTreats()); //Starting at offline mode, fetch images if any
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
            presenter.model.insertOrUpdateTreatsListToDb(serverTreats); //TODO: Improve local data base algorithm so deletion would not be required
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
            Log.d(TAG, "Treats received from initial call to server. size: " + serverTreats.size());
            presenter.fetchingMethodUnlocked = true;
            presenter.pagingEnabled = true;
            if (serverTreats.isEmpty() && presenter.model.getTreats().isEmpty()) {
                presenter.treatsListView.showSnackbarMessage(presenter.customResourcesProvider.getResources().getString(R.string.error_no_treats));
                presenter.pagingEnabled = false;
            }
            presenter.model.syncRealmWithServerTreats(presenter.treatsListView.getContext(), serverTreats, new GenericOperationCallback() {
                @Override
                public void onSuccess() {
                    //TODO: Improve local data base algorithm so deletion would not be required
                    presenter.getTreatListAdapterPresenter().setTreats(presenter.model.getTreats());
                    presenter.onFullTreatsResponseReceive(presenter.model.getTreats());
                }

                @Override
                public void onFailure(String errorForUser) {
                    Log.e(TAG, "Sync treats failed in Realm manager");
                    presenter.treatsListView.showSnackbarMessage(errorForUser);
                    presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                }
            });
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Treats retrieval error: " + fault.getDetail());
            presenter.treatsListView.onServerOperationFailed(presenter.errorsManager.getErrorFromFaultCode(fault.getCode(), presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active)));
        }
    };

    private AsyncCallback<List<Treat>> pagingCallback = new AsyncCallback<List<Treat>>() {
        @Override
        public void handleResponse(List<Treat> newTreats) {
            //TODO: implement a small progress treatsListView (like in facebook paging)
            presenter.fetchingMethodUnlocked = true;
            presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getPagingProgressDialog());
            if (newTreats.size() != 0) {
                presenter.model.getTreats().addAll(newTreats);
                presenter.model.insertOrUpdateTreatsListToDb(newTreats);
                presenter.onFullTreatsResponseReceive(newTreats);
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
     * Delets a treat from database or making it inivisble for non-puchasing users
     */

    void deleteTreatIfPossible(int treatPosition) {
        checkTreatAllPurchasesColumnInServer(treatPosition);
    }

    private void checkTreatAllPurchasesColumnInServer(int treatPosition) {
        Treat localTreat = presenter.model.getTreats().get(treatPosition);
        presenter.treatsStorage.findById(localTreat.getObjectId(), new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat serverTreat) {
                if (serverTreat.getAllPurchases() == 0) {
                    deleteTreatFromServer(localTreat, treatPosition);
                } else {
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
     * Set treat invisible to users.
     * Users who already purchased it may review it in the purchases history
     */
    void setTreatInvisible(int treatPosition) {
        Treat localTreat = presenter.model.getTreats().get(treatPosition);
        int prevPurchaseLimit = localTreat.getPurchasesLimit();
        localTreat.setVisible(false);
        localTreat.setPurchasesLimit(AppNumbers.NOT_PURCHASEABLE);
        presenter.treatsStorage.save(localTreat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat response) {
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                presenter.model.updateTreatInDb(localTreat);
                presenter.model.getTreats().remove(treatPosition);
                presenter.treatListAdapterPresenter.notifyDataSetChanged();
                presenter.treatsListView.showSnackbarMessage(presenter.customResourcesProvider.getResources().getString(R.string.treat_became_invisible));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                localTreat.setVisible(true);
                localTreat.setPurchasesLimit(prevPurchaseLimit);
                presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_delete_treat));
                Log.e(TAG, "Treat update failed. Reason: " + fault.getDetail());
            }
        });
    }

    private void deleteTreatFromServer(Treat treat, int treatPosition) {
        presenter.treatsStorage.remove(treat, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                presenter.treatsListView.dismissProgressDialog(presenter.treatsListView.getMainProgressDialog());
                presenter.model.deleteTreatFromDb(treat);
                presenter.model.getTreats().remove(treatPosition);
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
            BackendlessUser user = Hawk.get(AppStrings.KEY_LOGGED_IN_USER);
        try {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {
                    Log.i(TAG, "Login status check Successful. Is user logged in? " + response);
                    if (response && user.getObjectId() != null) {
                        fetchInitialTreats(initialTreatsDownloadCallback);
                        presenter.setUserDetailsInDataManager(user);
                        presenter.treatsListView.onUserLoggedIn();
                        return;
                    }
                    presenter.treatsListView.onUserLoggedOut();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    presenter.setUserDetailsInDataManager(user);
                    presenter.treatsListView.onServerOperationFailed(presenter.errorsManager.getErrorFromFaultCode(fault.getCode(), presenter.customResourcesProvider.getResources().getString(R.string.error_login_validation)));
                    Log.e(TAG, "Error in login validation. Reason: " + fault.getDetail());
                }
            });
        } catch (Exception e) {
            presenter.setUserDetailsInDataManager(user);
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
     * Purchases a treat
     */

    void purchaseTreat(BackendlessUser user, List<Treat> singleTreatInsideList, int treatPosition) {
        CodependentTasksManager tasksManager = new CodependentTasksManager(new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                Treat newTreat = singleTreatInsideList.get(0);
                presenter.model.getTreats().get(treatPosition).setUserPurchases(newTreat.getUserPurchases() + 1); //Local update
                presenter.model.getTreats().get(treatPosition).setAllPurchases(newTreat.getUserPurchases() + 1); //Local update
                presenter.model.updateTreatUserPurchasesInDb(newTreat);
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

    private void updateTreatTimesPurchased(Treat newTreat, CodependentTasksManager tasksManager) {
        newTreat.setAllPurchases(newTreat.getAllPurchases() + 1);
        presenter.treatsStorage.save(newTreat, new AsyncCallback<Treat>() {
            @Override
            public void handleResponse(Treat response) {
                Log.i(TAG, "Successfully updated allPurchases to " + newTreat.getAllPurchases() + " For treat with id: " + newTreat.getObjectId());
                tasksManager.onSingleOperationSuccessful();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                tasksManager.onSingleOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_purchase_treat));
                Log.e(TAG, "Could not update allPurchases. reason: " + fault.getDetail());
            }
        });
    }

    private void setRelationBetweenUserAndPurchasedTreat(BackendlessUser user, List<Treat> singleTreatInsideList, CodependentTasksManager tasksManager) {
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
     * Fetch the first treats from server according to a given paging offset.
     */

    public void fetchInitialTreats(AsyncCallback<List<Treat>> callback) {
        try {
            presenter.treatsStorage.find(presenter.allOwnerTreatsQueryBuilder, callback);
        } catch (Exception e) {
            e.printStackTrace();
            presenter.treatsListView.onServerOperationFailed(presenter.customResourcesProvider.getResources().getString(R.string.error_no_connection_and_local_db_active));
        }
    }

    /**
     * Fetch extra treats according to a given paging offset.
     */
    public void fetchPagingTreats(AsyncCallback<List<Treat>> callback) {
        if (presenter.pagingEnabled && presenter.fetchingMethodUnlocked) { //Could have been done if one boolean, but created 2 for different purposes
            presenter.treatsListView.showProgressDialog(presenter.treatsListView.getPagingProgressDialog());
            presenter.fetchingMethodUnlocked = false;
            presenter.treatsStorage.find(presenter.allOwnerTreatsQueryBuilder, callback);
        }
    }
}
