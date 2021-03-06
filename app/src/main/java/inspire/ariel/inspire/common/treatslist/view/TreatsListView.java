package inspire.ariel.inspire.common.treatslist.view;

import android.content.ContentResolver;
import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenter;

public interface TreatsListView {

    void showProgressDialog(KProgressHUD dialog);

    void dismissProgressDialog(KProgressHUD dialog);

    ContentResolver getContentResolver();

    void onServerOperationFailed(String error);

    void showReallyDeleteDialog(int treatPosition);

    void showEnterAdminPasswordDialog(Treat treat, int treatPosition);

    Context getContext();

    void scrollTreatListToTop();

    TreatsListPresenter getPresenter();

    void onUserLoggedOut();

    void onUserLoggedIn();

    KProgressHUD getLoginLogoutProgressDialog();

    KProgressHUD getMainProgressDialog();

    KProgressHUD getPagingProgressDialog();

    void showToastMessage(String messageForUser);

    void showSnackbarMessage(String message);

}
