package inspire.ariel.inspire.common.quoteslist.view;

import android.content.ContentResolver;
import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kaopiz.kprogresshud.KProgressHUD;

import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenter;

public interface QuotesListView {

    void showProgressDialog(KProgressHUD dialog);

    void dismissProgressDialog(KProgressHUD dialog);

    ContentResolver getContentResolver();

    void showReallyDeleteDialog(int quotePosition);

    Context getContext();

    void scrollQuoteListToTop();

    QuoteListPresenter getPresenter();

    void showToastErrorMessage(String string);

    void onServerOperationFailed(String error);

    void onUserLoggedOut();

    void onUserLoggedIn();

    KProgressHUD getLoginLogoutProgressDialog();

    KProgressHUD getMainProgressDialog();

    KProgressHUD getPagingProgressDialog();

}
