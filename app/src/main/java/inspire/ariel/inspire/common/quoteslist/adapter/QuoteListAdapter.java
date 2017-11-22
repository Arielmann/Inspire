package inspire.ariel.inspire.common.quoteslist.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.quoteslist.Quote;

public class QuoteListAdapter extends RecyclerView.Adapter<QuoteVH>{

    private List<Quote> dataSet;
    private Resources res;

    public QuoteListAdapter(List<Quote> dataSet, Resources res) {
        super();
        this.dataSet = dataSet;
        this.res = res;
    }

    @Override
    public QuoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_quote, parent, false);
        return new QuoteVH(view);
    }

    @Override
    public void onBindViewHolder(QuoteVH holder, int position) {
        holder.setUIDataOnView(dataSet.get(position), res);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
