package inspire.ariel.inspire.common.quoteslist.adapter;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

class QuoteVH extends RecyclerView.ViewHolder {

    private static final String TAG = QuoteVH.class.getSimpleName();

    QuoteVH(View itemView) {
        super(itemView);
    }

    public void setUIDataOnView(Quote quote, Resources res) {
        if(quote.getText() != null && !quote.getText().equals("")){
            TextView quoteTv = itemView.findViewById(R.id.quoteTv);
            quoteTv.setTextColor(Color.RED);
            quoteTv.setText(quote.getText());
            quoteTv.setTextColor(quote.getTextColor());
            FontsManager.getInstance().setFontOnTV(quote.getFontPath(), quoteTv);
            RelativeLayout quoteLayout = itemView.findViewById(R.id.quoteLayout);
            setLayoutParamsIfLongText(quoteLayout, quote.getText());
            quoteLayout.setBackground(quote.getImage());
            Log.d(TAG, quote.getText() + " is presented in the list as a quote");
        }
    }

    private void setLayoutParamsIfLongText(RelativeLayout layout, String text){
        if(text.length() > AppInts.LONG_TEXT){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(params);
        }
    }
}

