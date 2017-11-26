package inspire.ariel.inspire.common.quoteslist.view;

import android.content.ContentResolver;
import android.content.res.Resources;

import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;

public interface QuotesListView {

    void presentQuotesOnScreen(QuoteListAdapter adapter);
    void showQuoteRefreshErrorMessage();
    void showProgressDialog();
    void showNoInternetConnectionMessage();
    Resources getResources();
    ContentResolver getContentResolver();

}
