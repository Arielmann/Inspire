package inspire.ariel.inspire.common.utils.listutils.genericadapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.common.utils.listutils.vh.SingleImageViewVH;

public class SingleBitmapListAdapter extends RecyclerView.Adapter<SingleImageViewVH> {

    private final int vhLayout;
    private List<Bitmap> bitmaps;
    private View.OnClickListener onImageClicked;

    public SingleBitmapListAdapter(List<Bitmap> bitmaps, int vhLayout, View.OnClickListener onImageClicked) {
        this.bitmaps = bitmaps;
        this.vhLayout= vhLayout;
        this.onImageClicked = onImageClicked;
    }

    public SingleBitmapListAdapter(List<Bitmap> bitmaps, int vhLayout) {
        this.bitmaps = bitmaps;
        this.vhLayout= vhLayout;
    }

    @Override
    public SingleImageViewVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(vhLayout, parent, false);
        return new SingleImageViewVH(view);
    }

    @Override
    public void onBindViewHolder(SingleImageViewVH holder, int position) {
        holder.setUIDataOnView(bitmaps.get(position), onImageClicked);
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }
}
