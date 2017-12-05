package inspire.ariel.inspire.common.quoteslist.view;

import android.content.Context;

public interface QuoteListViewInjector {
    Context getContext();

    android.support.v4.app.FragmentManager getSupportFragmentManager();
}
