package inspire.ariel.inspire.common.utils.listutils.gridview;

import android.support.v7.widget.GridLayoutManager;

import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;

public class SpanSizeLookUpForHeaderAndFooter extends GridLayoutManager.SpanSizeLookup {

    private HeaderRecyclerViewAdapter adapter;
    private GridLayoutManager manager;

    public SpanSizeLookUpForHeaderAndFooter(HeaderRecyclerViewAdapter adapter, GridLayoutManager manager) {
        this.adapter = adapter;
        this.manager = manager;
    }

    @Override
    public int getSpanSize(int position) {
       return adapter.isHeaderPosition(position) || adapter.isFooterPosition(position) ? manager.getSpanCount() : 1;
    }
}
