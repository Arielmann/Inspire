package inspire.ariel.inspire.owner.quotecreator.view.optionmenufragment;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import com.github.aakira.expandablelayout.ExpandableLayout;

import lombok.Data;
import lombok.NonNull;

@Data
public class QuoteMenuComponents {

    @NonNull private ImageButton imageButton;
    @NonNull private ExpandableLayout expandableLayout;
    @NonNull private RecyclerView recyclerView;

}
