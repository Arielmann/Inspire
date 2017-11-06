package inspire.ariel.inspire.leader.quotecreatorfrag.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;

public class BgItemsAdapter extends RecyclerView.Adapter<BgItemViewHolder> {

    List<Bitmap> dataSet;

    public BgItemsAdapter(List<Bitmap> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public BgItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_bg_item, parent, false);
        return new BgItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BgItemViewHolder holder, int position) {
        holder.setUIDataOnView(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
