package inspire.ariel.inspire.common.quoteslist.view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

public interface QuotesListView {

    void showQuoteRefreshErrorMessage();

    void showMainProgressDialog();

    void showPagingProgressDialog();

    void dismissMainProgressDialog();

    void dismissPagingProgressDialog();

    void showNoInternetConnectionMessage();

    Resources getResources();

    ContentResolver getContentResolver();

    DiscreteScrollView getQuotesListRv();

    Context getContext();

    void scrollQuoteListToTop();

    void showNoQuotesMessage();

}
