package inspire.ariel.inspire.common.quoteslist.presenter;


import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.leader.Leader;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.constants.AppStrings;

public class QuoteListPresenterImpl implements QuoteListPresenter {

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> leadersStorage;

    @Inject
    QuoteListModel model;

    private QuotesListView view;
    private QuoteListAdapter adapter;

    public QuoteListPresenterImpl(QuotesListView view, AppComponent appComponent) {
        this.view = view;
        appComponent.inject(this);
        retrieveLeaderQuotesFromServer();
    }


    private void retrieveLeaderQuotesFromServer() {

        //LoadRelationsQueryBuilder<Quote> loadRelationsQueryBuilder;
        //loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Quote.class);
        //loadRelationsQueryBuilder.setPageSize(25).setOffset(50);
        //loadRelationsQueryBuilder.setRelationName(AppStrings.BACKENDLESS_TABLE_LEADER_COLUMN_QUOTES);

        leadersStorage.findById(AppStrings.LEADER_DEVICE_ID, new AsyncCallback<Leader>() {
            @Override
            public void handleResponse(Leader leader) {
                adapter = new QuoteListAdapter(leader.getQuotes());
                Quote quote = new Quote();
                quote.setMessage("This is an extremely long quote it is soooooo long so I can test if it gets out of screen! I don't want an innocent user to come and read his quote but then find out during application's runtime that he cannot read it all because SOMEONE has messed up the coding and allowed too long quotes to disappear forever");
                leader.getQuotes().add(quote);
                model.setDataSet(leader.getQuotes());
                view.presentQuotesOnScreen(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                view.showErrorMessage();
            }
        });
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}
