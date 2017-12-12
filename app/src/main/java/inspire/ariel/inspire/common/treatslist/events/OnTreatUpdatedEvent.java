package inspire.ariel.inspire.common.treatslist.events;

import inspire.ariel.inspire.common.treatslist.Treat;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnTreatUpdatedEvent {
    Treat treat;
    int position;
}
