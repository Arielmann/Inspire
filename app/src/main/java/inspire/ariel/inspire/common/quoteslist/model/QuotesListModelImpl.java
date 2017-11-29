package inspire.ariel.inspire.common.quoteslist.model;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class QuotesListModelImpl implements QuoteListModel {

    /*
    * This singleton model holds the data related with
    * the quotes presented to the user.
    */

    private static QuotesListModelImpl model;
    @Getter @Setter private List<Quote> quotes;

    public static QuotesListModelImpl getInstance() {
        if (model == null) {
            model = new QuotesListModelImpl();
            model.quotes = new ArrayList<>();
        }
        return model;
    }

    private QuotesListModelImpl() {
    }
}


