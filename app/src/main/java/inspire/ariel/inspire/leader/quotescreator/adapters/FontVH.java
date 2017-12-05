package inspire.ariel.inspire.leader.quotescreator.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.leader.quotescreator.events.OnFontClicked;

class FontVH extends RecyclerView.ViewHolder {

    private static final String TAG = FontVH.class.getSimpleName();
    private OnFontClicked onFontClicked;
    private TextView tv;

    public FontVH(View itemView, OnFontClicked onTextViewClicked) {
        super(itemView);
        this.onFontClicked = onTextViewClicked;
        tv = itemView.findViewById(R.id.singleTv);
    }

    public void setUIDataOnView(FontsManager.Font font) {
        tv.setText(font.getName());
        FontsManager.getInstance().setFontOnTV(font, tv);
        tv.setOnClickListener(view -> onFontClicked.onClick(font));
        Log.d(TAG, "Font view holder bound for font: " + font.getName());
    }

}

