package inspire.ariel.inspire.common.quoteslist.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;

public interface QuotesListView {

    void presentQuotesOnScreen(QuoteListAdapter adapter);
    void showErrorMessage();
    Resources getResources();
    ContentResolver getContentResolver();
}
