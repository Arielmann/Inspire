package inspire.ariel.inspire.common.quoteslist.events;

import java.util.List;

public class OnQuotesLoadedEvent {

    public List quotes;

    public OnQuotesLoadedEvent(List groupRows) {
        this.quotes = groupRows;
    }
}
