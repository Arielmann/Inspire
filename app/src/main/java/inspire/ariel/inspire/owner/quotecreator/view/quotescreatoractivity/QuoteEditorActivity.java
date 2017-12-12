package inspire.ariel.inspire.owner.quotecreator.view.quotescreatoractivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

public class QuoteEditorActivity extends QuotesCreatorActivity {

    private Quote oldQuote;
    private int oldQuotePosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldQuote = getIntent().getParcelableExtra(AppStrings.KEY_QUOTE);
        oldQuotePosition = getIntent().getIntExtra(AppStrings.KEY_QUOTE_POSITION, AppNumbers.ERROR_INT);
        initAtEditMode();
    }

    @Override
    void requestQuotePost(){
      Quote newQuote = super.createQuoteForPost();
      newQuote.setObjectId(oldQuote.getObjectId());
      presenter.requestUpdateQuote(newQuote, oldQuotePosition);
    }

    private void initAtEditMode() {
        super.getBinding().quoteEditText.setText(oldQuote.getText());
        super.getBinding().quoteEditText.setTextColor(oldQuote.getTextColor());
        super.getBinding().quoteEditText.setHintTextColor(oldQuote.getTextColor());
        super.getBinding().quoteEditText.setTextSize(oldQuote.getTextSize());
        super.getBinding().creatorLayout.setBackground(oldQuote.getImage());
        FontsManager.getInstance().setFontOnTV(oldQuote.getFontPath(), super.getBinding().quoteEditText);
        presenter.setFontPath(oldQuote.getFontPath());
    }
}
