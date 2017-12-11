package inspire.ariel.inspire.common.quoteslist.presenter;

import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapterPresenter;

public interface QuoteListPresenter {

    void init(QuoteListAdapterPresenter adapterPresenter);

    void onDestroy();

    void OnNewIntent(Intent intent);

    void logout();

    void onStart();

    void onStop();

    void login(CharSequence password);

    List<Quote> getQuotes();

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollChangedListener();

    void deleteQuote(int quotePosition);
}
