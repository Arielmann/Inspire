package inspire.ariel.inspire.common.utils.listutils.genericadapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.utils.listutils.vh.SingleImageViewButtonVH;

public class SingleBitmapListAdapter extends RecyclerView.Adapter<SingleImageViewButtonVH> {

    private final int vhLayout;
    private List<Bitmap> bitmaps;

    public SingleBitmapListAdapter(List<Bitmap> bitmaps, int vhLayout) {
        this.bitmaps = bitmaps;
        this.vhLayout= vhLayout;
    }

    @Override
    public SingleImageViewButtonVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(vhLayout, parent, false);
        return new SingleImageViewButtonVH(view);
    }

    @Override
    public void onBindViewHolder(SingleImageViewButtonVH holder, int position) {
        holder.setUIDataOnView(bitmaps.get(position));
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }
}
