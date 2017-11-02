package inspire.ariel.inspire.common.quoteslist.model;

import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;

public interface QuotesModel {
    void setDataSet(List<Quote> dataSet);
    List<Quote> getDataSet();
}
