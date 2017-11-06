package inspire.ariel.inspire.common.quoteslist.model;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;

public class QuotesListModelImpl implements QuoteListModel {

    /*
    * This singleton model holds the data related with
    * the quotes presented to the user.
    */

    private static QuotesListModelImpl model;
    private static List<Quote> quotes;

    public static QuotesListModelImpl getInstance() {
        if (model == null) {
            model = new QuotesListModelImpl();
            quotes = new ArrayList<>();
        }
        return model;
    }

    private QuotesListModelImpl() {
    }

    @Override
    public void setDataSet(List<Quote> dataSet) {
        QuotesListModelImpl.quotes = dataSet;
    }

    @Override
    public List<Quote> getDataSet() {
        return quotes;
    }
}


