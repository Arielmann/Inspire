package inspire.ariel.inspire.common.utils.listutils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.common.utils.imageutils.InspireBackgroundImage;

public class SingleBitmapListAdapter extends RecyclerView.Adapter<SingleImageViewVH> {

    private final int vhLayout;
    private List<InspireBackgroundImage> bgImages;
    public SingleBitmapListAdapter(List<InspireBackgroundImage> bgImages, int vhLayout) {
        this.bgImages = bgImages;
        this.vhLayout= vhLayout;
    }

    @Override
    public SingleImageViewVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(vhLayout, parent, false);
        return new SingleImageViewVH(view);
    }

    @Override
    public void onBindViewHolder(SingleImageViewVH holder, int position) {
        holder.setUIDataOnView(bgImages.get(position).getDrawable());
    }

    @Override
    public int getItemCount() {
        return bgImages.size();
    }
}
