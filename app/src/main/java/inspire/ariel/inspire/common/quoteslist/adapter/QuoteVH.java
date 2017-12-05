package inspire.ariel.inspire.common.quoteslist.adapter;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.singlequote.SingleQuoteActivity;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

class QuoteVH extends RecyclerView.ViewHolder {

    private static final String TAG = QuoteVH.class.getSimpleName();

    private EllipsizingTextView quoteTv;
    private RelativeLayout quoteLayout;
    private Button continueRead;

    QuoteVH(View itemView) {
        super(itemView);
        quoteTv = itemView.findViewById(R.id.quoteTv);
        quoteLayout = itemView.findViewById(R.id.quoteLayout);
        continueRead = itemView.findViewById(R.id.continueReadingBtn);
        quoteTv.addEllipsizeListener(ellipsized -> {
            if (ellipsized) {
                continueRead.setClickable(true);
                continueRead.setVisibility(View.VISIBLE);
            } else {
                continueRead.setClickable(false);
                continueRead.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setUIDataOnView(Quote quote) {
        quoteTv.setText(quote.getText());
        quoteTv.setTextColor(quote.getTextColor());
        quoteTv.setTextSize(quote.getTextSize());
        FontsManager.getInstance().setFontOnTV(quote.getFontPath(), quoteTv);
        quoteLayout.setBackground(quote.getImage());
        continueRead.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), SingleQuoteActivity.class);
            intent.putExtra(AppStrings.KEY_QUOTE, quote);
            itemView.getContext().startActivity(intent);
        });

        Log.d(TAG, quote.getText() + " is presented in the list as a quote");
    }
}

