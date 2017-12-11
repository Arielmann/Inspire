package inspire.ariel.inspire.common.quoteslist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.databinding.VhQuoteBinding;
import inspire.ariel.inspire.databinding.VhQuoteOptionsBinding;
import lombok.Setter;

public class QuoteListAdapter extends RecyclerView.Adapter<QuoteVH> implements QuoteListAdapterPresenter {

    @Setter private List<Quote> quotes;

    public QuoteListAdapter() {
        quotes = new ArrayList<>();
    }

    @Override
    public QuoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        VhQuoteBinding quoteBinding = VhQuoteBinding.inflate(layoutInflater, parent, false);
        VhQuoteOptionsBinding quoteOptionsManagerBinding = VhQuoteOptionsBinding.inflate(layoutInflater, parent, false);
//        View view = layoutInflater.inflate(R.layout.vh_quote_options, parent);
        return new QuoteVH(quoteBinding, quoteOptionsManagerBinding);
    }

    @Override
    public void onBindViewHolder(QuoteVH holder, int position) {
        holder.setUIDataOnView(quotes.get(position), position);
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }
}
