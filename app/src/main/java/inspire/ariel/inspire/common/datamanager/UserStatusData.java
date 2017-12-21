package inspire.ariel.inspire.common.datamanager;

import android.graphics.Color;
import android.view.View;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class UserStatusData {

    /**
     * This class has only 3 available instances,
     * 1. For user data
     * 2. For unautherized user
     * 3. For admin data
     */

    @Getter private int goToTreatDesignerVisibility;
    @Getter private boolean purchaseBtnClickable;
    @Getter private int purchaseBtnColor;
    @Getter private int treatOptionsManagerVisibility;

    @Getter static UserStatusData unAuthorizedUserData = UserStatusData.builder()
            .goToTreatDesignerVisibility(View.GONE)
            .goToTreatDesignerVisibility(View.GONE)
            .purchaseBtnClickable(false)
            .purchaseBtnColor(Color.GRAY).build();

    @Getter static UserStatusData normalUserData = UserStatusData.builder()
            .treatOptionsManagerVisibility(View.GONE)
            .goToTreatDesignerVisibility(View.GONE)
            .purchaseBtnClickable(true)
            .purchaseBtnColor(Color.GREEN).build();

    @Getter static UserStatusData adminDataUserData = UserStatusData.builder()
            .treatOptionsManagerVisibility(View.VISIBLE)
            .goToTreatDesignerVisibility(View.VISIBLE)
            .purchaseBtnClickable(true)
            .purchaseBtnColor(Color.GREEN).build();
}
