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
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapterPresenter;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.backendutils.CheckLoggedInCallback;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
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
    private QuotesListView view;
    private boolean isFetchingMethodUnlocked;

    public QuoteListPresenterImpl(AppComponent appComponent, QuotesListView view) {
        appComponent.inject(this);
        isFetchingMethodUnlocked = true;
        this.view = view;
    }

    /**
     * Init
     **/

    @Override
    public void init(QuoteListAdapterPresenter adapterPresenter) {
        this.quoteListAdapterPresenter = adapterPresenter;
        this.quoteListAdapterPresenter.setQuotes(model.getQuotes());
        fetchInitialQuotes(initialQuotesDownloadCallback);
    }

    private void initQuotesImages(List<Quote> quotes) {
        for (Quote quote : quotes) {
            Uri uri = Uri.parse(AppStrings.PREFIX_DRAWABLE_PATH + quote.getBgImageName()); //TODO: try catch for failures
            quote.setImage(ImageUtils.createDrawableFromUri(uri, view.getContentResolver(), customResourcesProvider.getResources()));
        }
    }

    /**
     * Lifecycle Methods
     **/
    @Override
    public void onDestroy() {
        view = null;
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
            view.scrollQuoteListToTop();
        } else {
            view.showToastErrorMessage(customResourcesProvider.getResources().getString(R.string.error_quote_refresh));
        }
        DataManager.getInstance().setMessagesSize(0);
    }

    /**
     * Server Communication
     * Not Thread Safe
     */
    public void fetchPagingQuotes(AsyncCallback<List<Quote>> callback) {
        if (isFetchingMethodUnlocked) {
            view.showPagingProgressDialog();
            isFetchingMethodUnlocked = false;
            quotesStorage.find(quotesQueryBuilder, callback);
        }
    }

    public void fetchInitialQuotes(AsyncCallback<List<Quote>> callback) {
        try {
            quotesStorage.find(quotesQueryBuilder, callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @SuppressWarnings("FieldCanBeLocal")
    private AsyncCallback<List<Quote>> initialQuotesDownloadCallback = new AsyncCallback<List<Quote>>() {
        @Override
        public void handleResponse(List<Quote> serverQuotes) {
            isFetchingMethodUnlocked = true;
            if (serverQuotes.size() == 0 && model.getQuotes().size() == 0) {
                view.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_quotes));
                return;
            }
            onFullQuotesResponseReceive(serverQuotes);
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Quotes retrieval error: " + fault.getDetail());
            view.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    private AsyncCallback<List<Quote>> pagingCallback = new AsyncCallback<List<Quote>>() {
        @Override
        public void handleResponse(List<Quote> serverQuotes) {
            //TODO: implement a small progress view (like in facebook paging)
            isFetchingMethodUnlocked = true;
            view.dismissPagingProgressDialog();
            if (serverQuotes.size() != 0) {
                onFullQuotesResponseReceive(serverQuotes);
            }
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Quotes retrieval error: " + fault.getDetail());
            isFetchingMethodUnlocked = true;
            view.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.error_no_connection));
        }
    };

    private void onFullQuotesResponseReceive(List<Quote> serverQuotes) {
        model.getQuotes().addAll(serverQuotes);
        quotesQueryBuilder.prepareNextPage();
        initQuotesImages(serverQuotes);
        quoteListAdapterPresenter.notifyDataSetChanged();
        view.dismissMainProgressDialog();
    }

    @Override
    public void login(CharSequence password, GenericOperationCallback callback) {

        Backendless.UserService.login("james.bond@mi6.co.uk", String.valueOf(password), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {
                callback.onSuccess();
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.onFailure(fault.getDetail());
            }
        }, true);
    }

    @Override
    public void checkIfUserLoggedIn(CheckLoggedInCallback checkLoggedInCallback) {
        try {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean response) {
                    checkLoggedInCallback.onUserStatusReceived(response);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    checkLoggedInCallback.onFailure(fault.getDetail());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logout(GenericOperationCallback callback) {

        Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if (response) {
                    Backendless.UserService.logout(new AsyncCallback<Void>() {
                        public void handleResponse(Void response) {
                            callback.onSuccess();
                        }

                        public void handleFault(BackendlessFault fault) {
                            callback.onFailure(fault.getDetail());
                        }
                    });
                } else {
                    Log.i(TAG, "User was already logged out from server. operation auto-succeeds");
                    callback.onSuccess();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.onFailure(fault.getDetail());
            }
        });
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
            if (adapterPosition == (model.getQuotes().size() - 1) && networkHelper.hasNetworkAccess(view.getContext())) {
                try {
                    fetchPagingQuotes(pagingCallback);
                }catch (Exception e){
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
