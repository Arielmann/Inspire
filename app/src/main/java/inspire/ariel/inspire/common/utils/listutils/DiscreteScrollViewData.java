package inspire.ariel.inspire.common.utils.listutils;

import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data @Builder
public class DiscreteScrollViewData {

    @NonNull private Orientation orientation;
    private int timeMillis;
    private int offScreenItems;
    private boolean hasFixedSize;
    private boolean setSlideOnFling;
    @NonNull private ScaleTransformer scaleTransformer;

}
