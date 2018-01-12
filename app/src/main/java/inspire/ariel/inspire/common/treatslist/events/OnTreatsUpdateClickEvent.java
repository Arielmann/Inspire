package inspire.ariel.inspire.common.treatslist.events;

import inspire.ariel.inspire.common.Treat;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnTreatsUpdateClickEvent {
    Treat treat;
    int position;
}
