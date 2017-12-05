package inspire.ariel.inspire.leader.quotescreator.view.optionmenufragment;

import android.content.res.AssetManager;

import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorViewController;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorViewQuoteProperties;

public interface QuoteCreatorMenuView {

    void initView();

    void setQuotesCreatorActivityViewProperties(QuotesCreatorViewQuoteProperties quotesCreatorView);

    void collapseAllOptionLayouts();

    boolean areAllOptionLayoutsCollapsed();

    void willInject(AppComponent component, QuotesCreatorViewController controller, AssetManager assetManager);
}
