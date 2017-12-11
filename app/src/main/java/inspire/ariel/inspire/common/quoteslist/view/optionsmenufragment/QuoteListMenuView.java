package inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment;

import android.graphics.drawable.Drawable;
import android.view.View;

import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;

public interface QuoteListMenuView {

    void init(QuotesListView quotesListView);

    void resetLoginLogoutBtn(Drawable imgDrawable, View.OnClickListener newListener);

    View.OnClickListener getOnLoginClicked();

    View.OnClickListener getOnLogoutClicked();

}
