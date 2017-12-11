package inspire.ariel.inspire.owner.quotecreator.view.optionmenufragment;

import android.content.res.AssetManager;

import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.owner.quotecreator.view.quotescreatoractivity.QuotesCreatorViewController;
import inspire.ariel.inspire.owner.quotecreator.view.quotescreatoractivity.QuotesCreatorViewQuoteProperties;

public interface QuoteCreatorMenuView {

    void initView();

    void setQuotesCreatorActivityViewProperties(QuotesCreatorViewQuoteProperties quotesCreatorView);

    void collapseAllOptionLayouts();

    boolean areAllOptionLayoutsCollapsed();

    void willInject(AppComponent component, QuotesCreatorViewController controller, AssetManager assetManager);
}
