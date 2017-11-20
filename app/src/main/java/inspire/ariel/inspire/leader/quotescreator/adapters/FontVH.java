package inspire.ariel.inspire.leader.quotescreator.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.listutils.vh.SingleImageViewVH;
import inspire.ariel.inspire.leader.quotescreator.events.OnFontClicked;

public class FontVH extends RecyclerView.ViewHolder {

    private static final String TAG = SingleImageViewVH.class.getSimpleName();
    private OnFontClicked onTextViewClicked;

    public FontVH(View itemView, OnFontClicked onTextViewClicked) {
        super(itemView);
        this.onTextViewClicked = onTextViewClicked;
    }

    public void setUIDataOnView(FontsManager.Font font) {
        TextView tv = itemView.findViewById(R.id.singleTv);
        tv.setText(font.getName());
        FontsManager.getInstance().setFontOnTV(font, tv);
        tv.setOnClickListener(view -> onTextViewClicked.onClick(font));
        Log.d(TAG, "Font view holder bound for font: " + font.getName());
    }
}
