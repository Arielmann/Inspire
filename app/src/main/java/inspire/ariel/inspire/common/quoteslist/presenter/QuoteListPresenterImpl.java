package inspire.ariel.inspire.common.quoteslist.presenter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.leader.Leader;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.constants.AppStrings;

public class QuoteListPresenterImpl implements QuoteListPresenter{

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
                adapter = new QuoteListAdapter(leader.getQuotes(), customResourcesProvider.getResources());
                for (Quote quote: leader.getQuotes()) {
                    Uri uri = Uri.parse(AppStrings.DRAWABLE_PATH_PREFIX + quote.getBgImageName());
                    quote.setImage(createDrawableFromUri(uri));
                }
                leader.getQuotes().add(createLongMockQuote());
                model.setDataSet(leader.getQuotes());
                view.presentQuotesOnScreen(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                view.showErrorMessage();
            }
        });
    }

    private Drawable createDrawableFromUri(Uri uri){
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

    private Quote createLongMockQuote(){
        Quote quote = new Quote();
        quote.setBgImageName(AppStrings.BLUE_YELLOW_BG);
        Uri uri = Uri.parse(AppStrings.DRAWABLE_PATH_PREFIX + quote.getBgImageName());
        quote.setImage(createDrawableFromUri(uri));
        quote.setText("This is an extremely long quote it is soooooo long so I can test if it gets out of screen! I don't want an innocent user to come and read his quote but then find out during application's runtime that he cannot read it all because SOMEONE has messed up the coding and allowed too long quotes to disappear forever");
        quote.setFontPath(FontsManager.Font.QUIRLYCUES.getPath());
        quote.setTextSize(AppInts.FORTY);
        quote.setTextColor(Color.BLACK);
        return quote;
    }
}
