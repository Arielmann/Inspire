package inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public interface TreatsCreatorViewController {

    void setBackground(Drawable newBackground);

    void dismissProgressDialogAndShowErrorMessage(String message);

    void goToOtherActivity(Intent otherActivityData);

    void dismissProgressDialog();

    void showProgressDialog();

    Context getContext();

    void showErrorDialogAndGoBackToTreatListActivity();

    void sendResultToActivity(Intent intent);
}
