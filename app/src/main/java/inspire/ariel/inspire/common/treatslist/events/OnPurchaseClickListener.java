package inspire.ariel.inspire.common.treatslist.events;

import inspire.ariel.inspire.common.treatslist.Treat;

public interface OnPurchaseClickListener {
    void onClick(Treat treat, int treatPosition);
}
