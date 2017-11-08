package inspire.ariel.inspire.leader.quotecreatorfrag.view;

import android.graphics.Bitmap;
import android.widget.EditText;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

public interface QuotesCreatorView {

    void changeBackground(Bitmap newBackground);
    void changeQuoteTextSize(int value);
    DiscreteScrollView getBgPicker();
    EditText getQuoteEditText();
}
