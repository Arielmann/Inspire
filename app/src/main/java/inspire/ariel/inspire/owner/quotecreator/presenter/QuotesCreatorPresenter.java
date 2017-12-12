package inspire.ariel.inspire.owner.quotecreator.presenter;

import android.graphics.drawable.Drawable;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.quoteslist.Quote;

public interface QuotesCreatorPresenter {

    void onDestroy();

    void requestPostQuote(Quote quote);

    String getBgImgName();

    String getFontPath();

    Drawable getChosenBgImage();

    void requestUpdateQuote(Quote quote, int position);

    void onQuoteFontClicked(String path);

    DiscreteScrollView.OnItemChangedListener<?> getOnItemChangedListener();

    void setFontPath(String path);
}
