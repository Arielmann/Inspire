package inspire.ariel.inspire.leader.quotescreator.presenter;

import android.graphics.drawable.Drawable;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.quoteslist.Quote;

public interface QuotesCreatorPresenter {

    void onDestroy();

    void postQuote(Quote quote);

    boolean validateQuote(String text);

    String getBgImgName();

    String getFontPath();

    Drawable getChosenBgImage();

    void onQuoteFontClicked(String path);

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollStateChangedListener();

    DiscreteScrollView.OnItemChangedListener<?> getOnItemChangedListener();

}
