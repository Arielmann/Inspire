package inspire.ariel.inspire.common.quoteslist.adapter;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.utils.listutils.ListPresentable;
import inspire.ariel.inspire.common.utils.listutils.vh.GenericViewHolder;

class QuoteViewHolder extends GenericViewHolder {

    private static final String TAG = QuoteViewHolder.class.getSimpleName();

    QuoteViewHolder(View itemView) {
        super(itemView);
    }

    public void setUIDataOnView(ListPresentable data) {
        final Quote quote = (Quote) data;
        if(quote.getMessage() != null && !quote.getMessage().equals("")){
            TextView quoteTv = (TextView) itemView.findViewById(R.id.quoteTv);
            quoteTv.setText(quote.getMessage());
            Log.d(TAG, quote.getMessage() + " is presented in the list as a quote");
        }
    }
}

