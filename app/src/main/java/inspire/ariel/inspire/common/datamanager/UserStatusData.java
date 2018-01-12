package inspire.ariel.inspire.common.datamanager;

import android.graphics.Color;
import android.view.View;

import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
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
    @Getter
    private int goToTreatDesignerVisibility;
 /*   @Getter
    private int purchaseBtnVisibility;*/
    @Getter
    private int treatOptionsVisibility;

    @Getter
    static UserStatusData unAuthorizedUserData = UserStatusData.builder()
            .goToTreatDesignerVisibility(View.GONE)
            .treatOptionsVisibility(View.GONE)
            .build();

    @Getter
    static UserStatusData normalUserData = UserStatusData.builder()
            .goToTreatDesignerVisibility(View.GONE)
            .treatOptionsVisibility(View.GONE)
            .build();

    @Getter
    static UserStatusData adminDataUserData = UserStatusData.builder()
            .goToTreatDesignerVisibility(View.VISIBLE)
            .treatOptionsVisibility(View.VISIBLE)
            .build();
}
