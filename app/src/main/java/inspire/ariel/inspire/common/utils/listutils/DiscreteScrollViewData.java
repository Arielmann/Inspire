package inspire.ariel.inspire.common.utils.listutils;

import android.support.v7.widget.OrientationHelper;

import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data @Builder
public class DiscreteScrollViewData {

    @NonNull private Orientation orientation;
    @Getter private int timeMillis;
    @Getter private int offScreenItems;
    @NonNull boolean hasFixedSize;
    @NonNull boolean setSlideOnFling;
    @NonNull private ScaleTransformer scaleTransformer;

}
