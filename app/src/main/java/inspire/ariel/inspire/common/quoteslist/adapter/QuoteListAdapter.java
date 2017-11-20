package inspire.ariel.inspire.common.quoteslist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.utils.listutils.vh.GenericVH;

public class QuoteListAdapter extends RecyclerView.Adapter<GenericVH>{

    private List<Quote> dataSet;

    public QuoteListAdapter(List<Quote> dataSet) {
        super();
        this.dataSet = dataSet;
    }

    @Override
    public GenericVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenericVH holder, int position) {
        holder.setUIDataOnView(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
