package inspire.ariel.inspire.common.treatslist.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.treatslist.events.OnPurchaseClickListener;
import inspire.ariel.inspire.databinding.VhAdminTreatOptionsBinding;
import inspire.ariel.inspire.databinding.VhTreatBinding;
import lombok.Setter;

public class TreatListAdapter extends RecyclerView.Adapter<TreatVH> implements TreatListAdapterPresenter {

    private static final String TAG = TreatListAdapter.class.getName();
    @Setter private List<Treat> treats;
    @Setter private OnPurchaseClickListener onPurchaseClickListener;

    public TreatListAdapter() {
        treats = new ArrayList<>();
    }

    @Override
    public TreatVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        VhTreatBinding quoteBinding = VhTreatBinding.inflate(layoutInflater, parent, false);
        VhAdminTreatOptionsBinding quoteOptionsManagerBinding = VhAdminTreatOptionsBinding.inflate(layoutInflater, parent, false);
        return new TreatVH(quoteBinding, quoteOptionsManagerBinding, onPurchaseClickListener);
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
