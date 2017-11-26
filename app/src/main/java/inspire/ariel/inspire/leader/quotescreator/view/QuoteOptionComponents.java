package inspire.ariel.inspire.leader.quotescreator.view;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import com.github.aakira.expandablelayout.ExpandableLayout;

import lombok.Data;
import lombok.NonNull;

@Data
public class QuoteOptionComponents {

    @NonNull private ImageButton imageButton;
    @NonNull private ExpandableLayout expandableLayout;
    @NonNull private RecyclerView recyclerView;

}
