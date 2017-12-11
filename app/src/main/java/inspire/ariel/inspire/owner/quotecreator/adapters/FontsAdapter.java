package inspire.ariel.inspire.owner.quotecreator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.owner.quotecreator.events.OnFontClicked;

public class FontsAdapter extends RecyclerView.Adapter<FontVH> {
    private List<FontsManager.Font> fonts;
    private OnFontClicked onTextViewClicked;

    public FontsAdapter(List<FontsManager.Font> fonts, OnFontClicked onTextViewClicked) {
        this.fonts = fonts;
        this.onTextViewClicked = onTextViewClicked;
    }

    @Override
    public FontVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_single_tv, parent, false);
        return new FontVH(view, onTextViewClicked);
    }

    @Override
    public void onBindViewHolder(FontVH holder, int position) {
        holder.setUIDataOnView(fonts.get(position));
    }

    @Override
    public int getItemCount() {
        return fonts.size();
    }
}

