package inspire.ariel.inspire.common.treatslist.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class OnTreatDeleteClickedEvent {
    private int position;
}
