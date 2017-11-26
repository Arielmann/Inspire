package inspire.ariel.inspire.leader.quotescreator.view;

import android.support.v7.widget.RecyclerView;

public interface QuoteOptionsView {

    void setQuotesCreatorActivityView(QuotesCreatorViewForFragments quotesCreatorView);

    RecyclerView getQuoteTextSizesRV();

    RecyclerView getQuoteTextColorRV();

    RecyclerView getFontsRV();

}
