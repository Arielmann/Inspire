package inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public interface QuotesCreatorViewController {

    void setBackground(Drawable newBackground);

    void dismissProgressDialogAndShowErrorMessage(String message);

    void goToOtherActivity(Intent newQuote);

    void dismissProgressDialog();

    void showProgressDialog();

    Context getContext();


}
