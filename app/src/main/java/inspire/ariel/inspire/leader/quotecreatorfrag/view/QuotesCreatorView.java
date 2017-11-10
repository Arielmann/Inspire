package inspire.ariel.inspire.leader.quotecreatorfrag.view;

import android.graphics.Bitmap;
import android.widget.EditText;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

public interface QuotesCreatorView {

    void changeBackground(Bitmap newBackground);
    void changeQuoteTextSize(int value);
    void setBgPickerVisibility(int visibility);
    DiscreteScrollView getBgPicker();
    EditText getQuoteEditText();
  /*  QuoteOptionFrag getQuoteFontView();
    QuoteOptionFrag getQuoteColorView();
    QuoteOptionFrag getQuoteSizeView();*/
}
