package inspire.ariel.inspire.leader.quotescreator.view;

import android.support.v7.widget.RecyclerView;

public interface QuoteCreatorMenuView {

    RecyclerView getFontsRv();

    RecyclerView getTextColorRv();

    RecyclerView getTextSizesRv();

    void setQuotesCreatorActivityView(QuotesCreatorViewForFragments quotesCreatorView);

    void collapseAllOptionLayouts();

    boolean areAllOptionLayoutsCollapsed();
}
