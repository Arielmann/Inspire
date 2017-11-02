package inspire.ariel.inspire.common.quoteslist.view;

import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;

public interface QuotesListActivityView {

    void presentQuotesOnScreen(QuoteListAdapter adapter);
    void showErrorMessage();
}
