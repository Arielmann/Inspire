package inspire.ariel.inspire.leader.quotescreator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.leader.quotescreator.events.OnFontSizeClicked;
import inspire.ariel.inspire.common.utils.listutils.vh.FontSizeVH;

public class FontSizesAdapter extends RecyclerView.Adapter<FontSizeVH> {

    private List<ResourcesModule.Size> sizes;
    private OnFontSizeClicked onTextViewClicked;

    public FontSizesAdapter(List<ResourcesModule.Size> sizes, OnFontSizeClicked onTextViewClicked) {
        this.sizes = sizes;
        this.onTextViewClicked = onTextViewClicked;
    }

    @Override
    public FontSizeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_single_tv, parent, false);
        return new FontSizeVH(view, onTextViewClicked);
    }

    @Override
    public void onBindViewHolder(FontSizeVH holder, int position) {
        holder.setUIDataOnView(sizes.get(position));
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }
}


