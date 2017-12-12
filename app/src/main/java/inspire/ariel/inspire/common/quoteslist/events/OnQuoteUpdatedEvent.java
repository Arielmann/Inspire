package inspire.ariel.inspire.common.quoteslist.events;

import inspire.ariel.inspire.common.quoteslist.Quote;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnQuoteUpdatedEvent {
    Quote quote;
    int position;
}
