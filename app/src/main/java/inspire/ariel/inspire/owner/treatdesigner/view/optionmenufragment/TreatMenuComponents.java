package inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import com.github.aakira.expandablelayout.ExpandableLayout;

import lombok.Data;
import lombok.NonNull;

@Data
public class TreatMenuComponents {
    @NonNull private ImageButton imageButton;
    @NonNull private ExpandableLayout expandableLayout;
    @NonNull private RecyclerView recyclerView;
}
