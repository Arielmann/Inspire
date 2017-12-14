package inspire.ariel.inspire.common.datamanager;

import android.graphics.drawable.Drawable;

import lombok.Builder;
import lombok.Data;

class UserStatusData {

    private boolean isGoToTreatCreatorEnabled;
    private boolean isTreatOptionsEnabled;
    private Drawable loginLogoutDrawable;

    private UserStatusData() {
    }
}
