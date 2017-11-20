package inspire.ariel.inspire.leader.quotescreator.view;

import android.graphics.Bitmap;
import android.widget.EditText;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

public interface QuotesCreatorView {

    void setBackground(Bitmap newBackground);
    void setQuoteTextSize(int size);
    void setQuoteFont(FontsManager.Font font);
    void setQuoteTextColor(int color);
    void setBgPickerVisibility(int visibility);
    DiscreteScrollView getBgPicker();
    EditText getQuoteEditText();
}
