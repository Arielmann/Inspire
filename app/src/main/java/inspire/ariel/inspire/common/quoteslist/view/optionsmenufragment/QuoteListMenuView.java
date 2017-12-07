package inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment;

import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;

public interface QuoteListMenuView {

    void showLoginBtn();

    void init(QuotesListView quotesListView);

    void setLoginAvailable();

    void setLogoutAvailable();
}
