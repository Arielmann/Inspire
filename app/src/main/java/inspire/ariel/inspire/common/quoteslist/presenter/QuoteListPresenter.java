package inspire.ariel.inspire.common.quoteslist.presenter;

import android.content.Intent;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapterPresenter;
import inspire.ariel.inspire.common.utils.backendutils.CheckLoggedInCallback;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;

public interface QuoteListPresenter {

    void init(QuoteListAdapterPresenter adapterPresenter);

    void onDestroy();

    void OnNewIntent(Intent intent);

    void logout(GenericOperationCallback callback);

    List<Quote> getQuotes();

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollChangedListener();

    void login(CharSequence password, GenericOperationCallback callback);

    void checkIfUserLoggedIn(CheckLoggedInCallback checkLoggedInCallback);
}
