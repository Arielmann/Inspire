package inspire.ariel.inspire.common.treatslist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.databinding.VhTreatBinding;
import inspire.ariel.inspire.databinding.VhTreatOptionsBinding;
import lombok.Setter;

public class TreatListAdapter extends RecyclerView.Adapter<TreatVH> implements TreatListAdapterPresenter {

    @Setter private List<Treat> treats;

    public TreatListAdapter() {
        treats = new ArrayList<>();
    }

    @Override
    public TreatVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        VhTreatBinding quoteBinding = VhTreatBinding.inflate(layoutInflater, parent, false);
        VhTreatOptionsBinding quoteOptionsManagerBinding = VhTreatOptionsBinding.inflate(layoutInflater, parent, false);
//        View view = layoutInflater.inflate(R.layout.vh_treat_options, parent);
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
