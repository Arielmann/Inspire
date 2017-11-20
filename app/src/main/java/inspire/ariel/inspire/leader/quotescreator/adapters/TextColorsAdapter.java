package inspire.ariel.inspire.leader.quotescreator.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.leader.quotescreator.events.OnQuoteColorClicked;

public class TextColorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Integer> colors;
    private OnQuoteColorClicked onQuoteColorOptionClicked;

    public TextColorsAdapter(List<Integer> bitmaps, OnQuoteColorClicked onQuoteColorOptionClicked) {
        this.colors = bitmaps;
        this.onQuoteColorOptionClicked = onQuoteColorOptionClicked;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_empty, parent, false);
        return new RecyclerView.ViewHolder(view) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setBackgroundColor(colors.get(position));
        holder.itemView.setOnClickListener(view -> onQuoteColorOptionClicked.onClick(colors.get(position)));
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }
}
