package inspire.ariel.inspire.leader;

import com.backendless.BackendlessUser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.quoteslist.Quote;
import lombok.Data;
import lombok.NonNull;

@Data
public class Leader {

    @NonNull private String objectId;
    @NonNull private String name;
    @NonNull private String description;
    @NonNull private List<BackendlessUser> followers;
    @NonNull private List<Quote> quotes;
    //private Bitmap image = ImageUtils.defaultProfileImage;

    //Convenience Constructor
    public Leader() {
        this.name = "Leader Name";
        this.description = "Leader Description";
        this.followers = new ArrayList<>();
        this.followers.add(new BackendlessUser());
    }
}
