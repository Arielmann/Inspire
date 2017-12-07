package inspire.ariel.inspire.common.quoteslist.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenter;

public interface QuotesListView {

    void showMainProgressDialog();

    void showPagingProgressDialog();

    void dismissMainProgressDialog();

    void dismissPagingProgressDialog();

    ContentResolver getContentResolver();

    Context getContext();

    void scrollQuoteListToTop();

    QuoteListPresenter getPresenter();

    void showToastErrorMessage(String string);

    void onServerOperationFailed(String error);
}
