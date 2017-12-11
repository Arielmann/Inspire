package inspire.ariel.inspire.common.quoteslist.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class OnQuoteDeleteClickedEvent {
    private int position;
}
