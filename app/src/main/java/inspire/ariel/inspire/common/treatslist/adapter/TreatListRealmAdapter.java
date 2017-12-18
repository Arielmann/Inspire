/*
package inspire.ariel.inspire.common.treatslist.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.databinding.VhTreatBinding;
import inspire.ariel.inspire.databinding.VhTreatOptionsBinding;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import lombok.Setter;

public class TreatListRealmAdapter extends RealmRecyclerViewAdapter<Treat, TreatVH> implements TreatListRealmAdapterPresenter {

    @Setter private RealmResults<Treat> treats;

    public TreatListRealmAdapter(RealmResults<Treat> treats) {
        super(null, true, true);
        this.treats = treats;
    }

    @Override
    public TreatVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        VhTreatBinding quoteBinding = VhTreatBinding.inflate(layoutInflater, parent, false);
        VhTreatOptionsBinding quoteOptionsManagerBinding = VhTreatOptionsBinding.inflate(layoutInflater, parent, false);
        return new TreatVH(quoteBinding, quoteOptionsManagerBinding);
    }

    @Override
    public void onBindViewHolder(TreatVH holder, int position) {
        holder.setUIDataOnView(treats.get(position), position);
    }

    @Override
    public int getItemCount() {
        return treats.size();
    }
}
*/
