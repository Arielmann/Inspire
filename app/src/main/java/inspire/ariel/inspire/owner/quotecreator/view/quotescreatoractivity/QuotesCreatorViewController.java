package inspire.ariel.inspire.owner.quotecreator.view.quotescreatoractivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public interface QuotesCreatorViewController {

    void setBackground(Drawable newBackground);

    void dismissProgressDialogAndShowErrorMessage(String message);

    void goToOtherActivity(Intent otherActivityData);

    void dismissProgressDialog();

    void showProgressDialog();

    Context getContext();

    void showErrorDialogAndGoBackToQuoteListActivity();

    void sendResultToActivity(Intent intent);
}
