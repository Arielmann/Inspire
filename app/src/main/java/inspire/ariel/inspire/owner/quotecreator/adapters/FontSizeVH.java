package inspire.ariel.inspire.owner.quotecreator.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.owner.quotecreator.events.OnFontSizeClicked;

class FontSizeVH extends RecyclerView.ViewHolder {

    private static final String TAG = FontSizeVH.class.getSimpleName();
    private OnFontSizeClicked onTextViewClicked;
    private TextView tv;

    public FontSizeVH(View itemView, OnFontSizeClicked onTextViewClicked) {
        super(itemView);
        this.onTextViewClicked = onTextViewClicked;
        tv = itemView.findViewById(R.id.singleTv);
    }

    public void setUIDataOnView(ResourcesModule.Size fontSize) {
        tv.setText(String.valueOf(fontSize.getSize()));
        tv.setOnClickListener(view -> onTextViewClicked.onClick(fontSize));
        Log.d(TAG, "font size " + fontSize.getSize() + " was set on view");
    }
}
