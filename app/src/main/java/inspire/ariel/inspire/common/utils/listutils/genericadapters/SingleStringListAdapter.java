package inspire.ariel.inspire.common.utils.listutils.genericadapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.utils.listutils.vh.SingleTvVH;

public class SingleStringListAdapter extends RecyclerView.Adapter<SingleTvVH> {

    private List<String> strings;

    public SingleStringListAdapter(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public SingleTvVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_single_tv, parent, false);
        return new SingleTvVH(view);
    }

    @Override
    public void onBindViewHolder(SingleTvVH holder, int position) {
        holder.setUIDataOnView(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }
}


