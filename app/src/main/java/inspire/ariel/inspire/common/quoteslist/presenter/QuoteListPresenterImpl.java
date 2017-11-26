package inspire.ariel.inspire.common.quoteslist.presenter;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.leader.Leader;

public class QuoteListPresenterImpl implements QuoteListPresenter {

    private static final String TAG = QuoteListPresenterImpl.class.getName();

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> leadersStorage;

    @Inject
    QuoteListModel model;

    @Inject
    ResourcesProvider customResourcesProvider;

    private QuotesListView view;
    private QuoteListAdapter adapter;

    public QuoteListPresenterImpl(QuotesListView view, AppComponent appComponent) {
        this.view = view;
        appComponent.inject(this);
    }

    @Override
    public void retrieveLeaderQuotesFromServer() {

        //LoadRelationsQueryBuilder<Quote> loadRelationsQueryBuilder;
        //loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Quote.class);
        //loadRelationsQueryBuilder.setPageSize(25).setOffset(50);
        //loadRelationsQueryBuilder.setRelationName(AppStrings.BACKENDLESS_TABLE_LEADER_COLUMN_QUOTES);

        leadersStorage.findById(AppStrings.LEADER_DEVICE_ID, new AsyncCallback<Leader>() {
            @Override
            public void handleResponse(Leader leader) {
                compareServerLeaderWithDataManagerLeader(leader);
                adapter = new QuoteListAdapter(DataManager.getInstance().getLeader().getQuotes(), customResourcesProvider.getResources());
                model.setDataSet(DataManager.getInstance().getLeader().getQuotes());
                if (view != null) {
                    view.presentQuotesOnScreen(adapter);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                if (view != null) {
                    if (DataManager.getInstance().getLeader() == null) {
                        view.showNoInternetConnectionMessage();
                    } else {
                        view.showQuoteRefreshErrorMessage();
                    }
                }
            }
        });
    }

    private void compareServerLeaderWithDataManagerLeader(Leader serverLeader) {
        if (DataManager.getInstance().getLeader() == null) {
            updateDataManagerLeader(serverLeader);
            return;
        }

        if (!serverLeader.getQuotes().equals(DataManager.getInstance().getLeader().getQuotes())) {
            updateDataManagerLeader(serverLeader);
        }
    }

    private void addNoQuotesMessageTorLeaderQuotes(Leader leader) {
        Log.e(TAG, "Leader has no quotes");
        List<Quote> quotes = new ArrayList<>();
        quotes.add(Quote.newNoQuotesToPresentQuote(view.getResources()));
        leader.setQuotes(quotes);
    }

    private void updateDataManagerLeader(Leader serverLeader) {
        if (serverLeader.getQuotes().size() == 0) {
            addNoQuotesMessageTorLeaderQuotes(serverLeader);
        }
        initLeaderImages(serverLeader);
        DataManager.getInstance().setLeader(serverLeader);
    }

    private void initLeaderImages(Leader serverLeader) {
        for (Quote quote : serverLeader.getQuotes()) {
            Uri uri = Uri.parse(AppStrings.DRAWABLE_PATH_PREFIX + quote.getBgImageName()); //TODO: check for failures
            quote.setImage(createDrawableFromUri(uri));
        }
    }

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

    @Override
    public void onDestroy() {
        view = null;
    }

}
