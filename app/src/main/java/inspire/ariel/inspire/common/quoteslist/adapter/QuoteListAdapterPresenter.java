package inspire.ariel.inspire.common.quoteslist.adapter;

import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;

public interface QuoteListAdapterPresenter {

    void notifyDataSetChanged();

    void setQuotes(List<Quote> quotes);

}
