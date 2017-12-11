package inspire.ariel.inspire.common.quoteslist.presenter;

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
import com.orhanobut.hawk.Hawk;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapterPresenter;
import inspire.ariel.inspire.common.quoteslist.events.OnQuoteDeleteClickedEvent;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;
import lombok.Getter;

public class QuoteListPresenterImpl implements QuoteListPresenter {

    private static final String TAG = QuoteListPresenterImpl.class.getName();


    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> quotesStorage;

    @Inject
    QuoteListModel model;

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    DataQueryBuilder quotesQueryBuilder;

    @Inject
    NetworkHelper networkHelper;

    private QuoteListAdapterPresenter quoteListAdapterPresenter;
    private QuotesListView quotesListView;
    private boolean isFetchingMethodUnlocked;
    private int allServerQuotesListSize;
    private Map<String, String> errorsMap;

    public QuoteListPresenterImpl(AppComponent appComponent, QuotesListView view) {
        appComponent.inject(this);
        isFetchingMethodUnlocked = true;
        allServerQuotesListSize = Integer.MAX_VALUE;
        this.quotesListView = view;
    }

    /**
     * Init
     **/

    @Override
    public void init(QuoteListAdapterPresenter adapterPresenter) {
        this.quoteListAdapterPresenter = adapterPresenter;
        this.quoteListAdapterPresenter.setQuotes(model.getQuotes());
        initErrorsMap();
        fetchInitialQuotes(initialQuotesDownloadCallback);
        fetchDataSetSize();
    }

    private void initErrorsMap() {
        errorsMap = new HashMap<>();
        errorsMap.put(AppStrings.BACKENDLESS_ERROR_CODE_INVALID_LOGIN_OR_PASSWORD, customResourcesProvider.getResources().getString(R.string.error_invalid_login_or_password));
        errorsMap.put(AppStrings.BACKENDLESS_ERROR_CODE_EMPTY_PASSWORD_INPUT, customResourcesProvider.getResources().getString(R.string.error_empty_password_input));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuoteDeleteClickedEvent(OnQuoteDeleteClickedEvent event) {
        quotesListView.showReallyDeleteDialog(event.getPosition());
    }

    /**
     * Lifecycle Methods
     **/
    @Override
    public void onDestroy() {
        quotesListView = null;
    }

    @Override
    public void OnNewIntent(Intent intent) {
        if (intent.getParcelableExtra(AppStrings.KEY_QUOTE) != null) {
            Quote newQuote = intent.getParcelableExtra(AppStrings.KEY_QUOTE);
            initQuotesImages(new ArrayList<Quote>() {{
                add(newQuote);
            }});
            model.getQuotes().add(0, newQuote);
            quoteListAdapterPresenter.notifyDataSetChanged();
            quotesListView.scrollQuoteListToTop();
            DataManager.getInstance().setMessagesSize(0);
        }
    }

    @Override
    public void onStart() {
        if (Hawk.get(AppStrings.KEY_IS_USER_OWNER)) {
            EventBus.getDefault().register(this);
            checkIfUserLoggedIn();
        }
    }

    @Override
    public void onStop(){
        if (Hawk.get(AppStrings.KEY_IS_USER_OWNER)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * Server Communication
     * Not Thread Safe
     */
    public void fetchPagingQuotes(AsyncCallback<List<Quote>> callback) {
        if (isFetchingMethodUnlocked) {
            quotesListView.showProgressDialog(quotesListView.getPagingProgressDialog());
            isFetchingMethodUnlocked = false;
            quotesStorage.find(quotesQueryBuilder, callback);
        }
    }

    public void fetchInitialQuotes(AsyncCallback<List<Quote>> callback) {
        try {
            quotesStorage.find(quotesQueryBuilder, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("FieldCanBeLocal")
    private AsyncCallback<List<Quote>> initialQuotesDownloadCallback = new AsyncCallback<List<Quote>>() {
        @Override
        public void handleResponse(List<Quote> serverQuotes) {
            isFetchingMethodUnlocked = true;
            if (serverQuotes.size() == 0 && model.getQuotes().size() == 0) {
                quotesListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_quotes));
                return;
            }
            onFullQuotesResponseReceive(serverQuotes);
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Quotes retrieval error: " + fault.getDetail());
            quotesListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    private AsyncCallback<List<Quote>> pagingCallback = new AsyncCallback<List<Quote>>() {
        @Override
        public void handleResponse(List<Quote> serverQuotes) {
            //TODO: implement a small progress quotesListView (like in facebook paging)
            isFetchingMethodUnlocked = true;
            quotesListView.dismissProgressDialog(quotesListView.getPagingProgressDialog());
            if (serverQuotes.size() != 0) {
                onFullQuotesResponseReceive(serverQuotes);
            }
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Quotes retrieval error: " + fault.getDetail());
            isFetchingMethodUnlocked = true;
            quotesListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    private void onFullQuotesResponseReceive(List<Quote> serverQuotes) {
        model.getQuotes().addAll(serverQuotes);
        quotesQueryBuilder.prepareNextPage();
        initQuotesImages(serverQuotes);
        quoteListAdapterPresenter.notifyDataSetChanged();
        quotesListView.dismissProgressDialog(quotesListView.getMainProgressDialog());
    }

    @Override
    public void deleteQuote(int position){
        quotesStorage.remove(model.getQuotes().get(position), new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                quotesListView.dismissProgressDialog(quotesListView.getMainProgressDialog());
                model.getQuotes().remove(position);
                quoteListAdapterPresenter.notifyDataSetChanged();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                quotesListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_delete_quote));
                Log.e(TAG, "Quote deletion failed. Reason: " + fault.getDetail());
            }
        });
    }

    private void fetchDataSetSize() {
        quotesStorage.getObjectCount(new AsyncCallback<Integer>() {

            @Override
            public void handleResponse(Integer size) {
                Log.i(TAG, "Quotes list size in server: " + size);
                QuoteListPresenterImpl.this.allServerQuotesListSize = size;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error getting server quotes array size. size stays Integer.MAX_VALUE. error reason: " + fault.getDetail());
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
                        quotesListView.onUserLoggedIn();
                        return;
                    }
                    quotesListView.onUserLoggedOut();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    quotesListView.onUserLoggedOut();
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
                quotesListView.onUserLoggedIn();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Login failed. reason: " + fault.getDetail());
                if (errorsMap.containsKey(fault.getCode())) {
                    quotesListView.onServerOperationFailed(errorsMap.get(fault.getCode()));
                    return;
                }
                quotesListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.generic_error_login));
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
                            quotesListView.onUserLoggedOut();
                        }

                        public void handleFault(BackendlessFault fault) {
                            quotesListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_logout));
                            Log.e(TAG, "Logout failed. reason: " + fault.getDetail());
                        }
                    });
                } else {
                    Log.i(TAG, "User was already logged out from server. operation auto-succeeds");
                    quotesListView.onUserLoggedOut();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                quotesListView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_logout));
                Log.e(TAG, "Logout failed. reason: " + fault.getDetail());
            }
        });
    }

    /**
     * Image handling
     */

    private void initQuotesImages(List<Quote> quotes) {
        for (Quote quote : quotes) {
            Uri uri = Uri.parse(AppStrings.PREFIX_DRAWABLE_PATH + quote.getBgImageName()); //TODO: try catch for failures
            quote.setImage(ImageUtils.createDrawableFromUri(uri, quotesListView.getContentResolver(), customResourcesProvider.getResources()));
        }
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
            if (adapterPosition == (model.getQuotes().size() - 1) && networkHelper.hasNetworkAccess(quotesListView.getContext())) {
                try {
                    if(model.getQuotes().size() < allServerQuotesListSize) {
                        fetchPagingQuotes(pagingCallback);
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

    /**
     * Getters
     */

    @Override
    public List<Quote> getQuotes() {
        return model.getQuotes();
    }
}

