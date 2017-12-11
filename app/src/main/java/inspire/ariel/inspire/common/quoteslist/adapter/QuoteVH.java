package inspire.ariel.inspire.common.quoteslist.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.singlequote.SingleQuoteActivity;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.databinding.VhQuoteBinding;
import inspire.ariel.inspire.databinding.VhQuoteOptionsBinding;

class QuoteVH extends RecyclerView.ViewHolder {

    private static final String TAG = QuoteVH.class.getSimpleName();

    VhQuoteBinding quoteBinding;
    VhQuoteOptionsBinding quoteManagerOptionsBinding;

    public QuoteVH(VhQuoteBinding quoteBinding, VhQuoteOptionsBinding quoteOptionsManagerBinding) {
        super(quoteBinding.getRoot());
        this.quoteBinding = quoteBinding;
        this.quoteManagerOptionsBinding = quoteOptionsManagerBinding;
        this.quoteBinding.quoteTv.addEllipsizeListener(ellipsized -> {
            if (ellipsized) {
                this.quoteBinding.continueReadBtn.setVisibility(View.VISIBLE);
            } else {
                this.quoteBinding.continueReadBtn.setVisibility(View.GONE);
            }
        });
    }

    public void setUIDataOnView(Quote quote, int position) {
        quoteBinding.quoteTv.setText(quote.getText());
        quoteBinding.quoteTv.setTextColor(quote.getTextColor());
        quoteBinding.quoteTv.setTextSize(quote.getTextSize());
        FontsManager.getInstance().setFontOnTV(quote.getFontPath(), quoteBinding.quoteTv);
        quoteBinding.quoteLayout.setBackground(quote.getImage());
        quoteBinding.continueReadBtn.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), SingleQuoteActivity.class);
            intent.putExtra(AppStrings.KEY_QUOTE, quote);
            itemView.getContext().startActivity(intent);
        });

        quoteBinding.quoteManagerOptionsBtn.setOnClickListener(view -> {

            QuoteOptionsVH quoteOptionsVH = new QuoteOptionsVH(quoteManagerOptionsBinding);
            DialogPlus dialog = DialogPlus.newDialog(quoteBinding.getRoot().getContext())
                    .setContentHolder(quoteOptionsVH)
                    .setExpanded(true, quoteOptionsVH.getHeight())
                    .setGravity(Gravity.BOTTOM)
                    .create();
            dialog.show();

            quoteOptionsVH.init(dialog, quote, position); //Must call after dialog has shown
        });

        Log.d(TAG, quote.getText() + " is presented in the list as a quote");
    }
}

