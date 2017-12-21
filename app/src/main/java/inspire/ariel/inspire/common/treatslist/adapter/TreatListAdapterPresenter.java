package inspire.ariel.inspire.common.treatslist.adapter;

import java.util.List;

import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.treatslist.events.OnPurchaseClickListener;

public interface TreatListAdapterPresenter {

    void notifyDataSetChanged();

    void setTreats(List<Treat> treats);

    void setOnPurchaseClickListener(OnPurchaseClickListener listener);

}
