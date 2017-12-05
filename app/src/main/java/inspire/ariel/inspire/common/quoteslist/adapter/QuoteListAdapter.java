package inspire.ariel.inspire.common.quoteslist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.quoteslist.Quote;
import lombok.Setter;

public class QuoteListAdapter extends RecyclerView.Adapter<QuoteVH> implements QuoteListAdapterPresenter {

    @Setter private List<Quote> quotes;

    public QuoteListAdapter() {
        quotes = new ArrayList<>();
    }

    @Override
    public QuoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_quote, parent, false);
        return new QuoteVH(view);
    }

    @Override
    public void onBindViewHolder(QuoteVH holder, int position) {
        holder.setUIDataOnView(quotes.get(position));
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }
}
