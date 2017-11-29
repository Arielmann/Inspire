package inspire.ariel.inspire.leader;

import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.Quote;
import lombok.Data;
import lombok.NonNull;

@Data
public class Leader {

    @NonNull private String objectId;
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private List<BackendlessUser> followers;
    private List<Quote> quotes;

    public Leader() {
        this.objectId = AppStrings.VAL_LEADER_OBJECT_ID;
        this.name = AppStrings.VAL_LEADER_NAME;
        this.description = AppStrings.VAL_LEADER_DESCRIPTION;
        this.followers = new ArrayList<>();
    }
}
