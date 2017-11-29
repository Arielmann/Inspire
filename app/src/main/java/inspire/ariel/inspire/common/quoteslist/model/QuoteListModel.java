package inspire.ariel.inspire.common.quoteslist.model;

import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;

public interface QuoteListModel {
    List<Quote> getQuotes();
    void setQuotes(List<Quote> quotes);
}
