package inspire.ariel.inspire.common.quoteslist.presenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.leader.Leader;

public class QuoteListPresenterImpl implements QuoteListPresenter, DiscreteScrollView.ScrollStateChangeListener {

    private static final String TAG = QuoteListPresenterImpl.class.getName();

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> leadersStorage;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> quotesStorage;

    @Inject
    QuoteListModel model;

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    DataQueryBuilder quotesQueryBuilder;

    private QuotesListView view;
    private QuoteListAdapter adapter;
    private boolean isFetchingMethodUnlocked = true;

    public QuoteListPresenterImpl(QuotesListView view, AppComponent appComponent) {
        this.view = view;
        appComponent.inject(this);
        adapter = new QuoteListAdapter(model.getQuotes(), customResourcesProvider.getResources());
        initQuotesRv(view.getQuotesListRv());
        fetchInitialQuotes(initialQuotesDownloadCallback);
    }

    /**
     * Init
     **/
    private void initQuotesRv(DiscreteScrollView rv) {
        rv.setSlideOnFling(true);
        rv.setAdapter(adapter);
        rv.addScrollStateChangeListener(this);
        rv.setOrientation(Orientation.VERTICAL);
        rv.setItemTransitionTimeMillis(AppTimeMillis.QUARTER_SECOND);
        rv.setOffscreenItems(0);
        rv.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(AppInts.FLOAT_EIGHT_TENTHS)
                .build());
    }

    private void initQuotesImages(List<Quote> quotes) {
        for (Quote quote : quotes) {
            Uri uri = Uri.parse(AppStrings.PREFIX_DRAWABLE_PATH + quote.getBgImageName()); //TODO: try catch for failures
            quote.setImage(createDrawableFromUri(uri));
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
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getParcelable(AppStrings.KEY_QUOTE) != null) {
            Quote newQuote = extras.getParcelable(AppStrings.KEY_QUOTE);
            initQuotesImages(new ArrayList<Quote>(){{add(newQuote);}});
            model.getQuotes().add(AppInts.ZERO, newQuote);
            adapter.notifyDataSetChanged();
            view.scrollQuoteListToTop();
        } else {
            view.showQuoteRefreshErrorMessage();
        }
        DataManager.getInstance().setMessagesSize(AppInts.ZERO);
    }

    /**
     * Server Communication
     **/
    public void fetchPagingQuotes(AsyncCallback<List<Quote>> callback) {
        if (isFetchingMethodUnlocked) {
            view.showPagingProgressDialog();
            isFetchingMethodUnlocked = false;
            quotesStorage.find(quotesQueryBuilder, callback);
        }
    }

    public void fetchInitialQuotes(AsyncCallback<List<Quote>> callback) {
        quotesStorage.find(quotesQueryBuilder, callback);
    }


    @SuppressWarnings("FieldCanBeLocal")
    private AsyncCallback<List<Quote>> initialQuotesDownloadCallback = new AsyncCallback<List<Quote>>() {
        @Override
        public void handleResponse(List<Quote> serverQuotes) {
            isFetchingMethodUnlocked = true;
            if (serverQuotes.size() == AppInts.ZERO && model.getQuotes().size() == AppInts.ZERO) {
                addNoQuotesMessageToDataManagerQuotes();
                view.dismissMainProgressDialog();
                return;
            }
            onFullQuotesResponseReceive(serverQuotes);
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            Log.e(TAG, "Quotes retrieval error: " + fault.getMessage());
            view.showNoInternetConnectionMessage();
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
            Log.e(TAG, "Quotes retrieval error: " + fault.getMessage());
            isFetchingMethodUnlocked = true;
            view.dismissMainProgressDialog();
            if (view != null) {
                view.showNoInternetConnectionMessage();
            }
        }
    };

    private void onFullQuotesResponseReceive(List<Quote> serverQuotes) {
        //Todo: if serverQuotes list in data manager != 0, refresh so the quote in the current position will still be presented
        //Todo: Another option: Cancel quote download when pressing the back button from quote creator activity
        model.getQuotes().addAll(serverQuotes);
        quotesQueryBuilder.prepareNextPage();
        initQuotesImages(serverQuotes);
        adapter.notifyDataSetChanged();
        view.dismissMainProgressDialog();
    }

    private void addNoQuotesMessageToDataManagerQuotes() {
        Log.e(TAG, "Leader has no quotes");
        model.getQuotes().add(Quote.newNoQuotesToPresentQuote(customResourcesProvider.getResources()));
        adapter.notifyDataSetChanged();
    }

    /**
     * Image creation
     **/
    //TODO: Fix the bug that won't show the leader the correct quote background after upload
    private Drawable createDrawableFromUri(Uri uri) {
        Drawable drawable;
        try {
            InputStream inputStream = view.getContentResolver().openInputStream(uri);
            drawable = Drawable.createFromStream(inputStream, uri.toString());
        } catch (FileNotFoundException e) {
            drawable = view.getResources().getDrawable(R.drawable.blue_yellow_bg);
        }

        return drawable;
    }


    /**
     * Scrolling Methods
     **/
    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        if (adapterPosition == (model.getQuotes().size() - AppInts.ONE)) {
            fetchPagingQuotes(pagingCallback);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }
}
