package inspire.ariel.inspire.common.quoteslist.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapterPresenter;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;

public interface QuoteListPresenter {

    void onDestroy();

    void OnNewIntent(Intent intent);

    List<Quote> getQuotes();

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollChangedListener();
}
