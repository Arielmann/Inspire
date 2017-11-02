package inspire.ariel.inspire.common.utils.listutils.gridview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

public class GridViewLayoutWithHeaderAndFooter extends GridLayoutManager{

    public GridViewLayoutWithHeaderAndFooter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridViewLayoutWithHeaderAndFooter(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridViewLayoutWithHeaderAndFooter(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setSpanSizeLookup(SpanSizeLookUpForHeaderAndFooter spanSizeLookup) {
        super.setSpanSizeLookup(spanSizeLookup);
    }
}
