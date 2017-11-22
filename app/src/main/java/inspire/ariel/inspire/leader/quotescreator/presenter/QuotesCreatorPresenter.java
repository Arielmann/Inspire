package inspire.ariel.inspire.leader.quotescreator.presenter;

import inspire.ariel.inspire.common.quoteslist.Quote;

public interface QuotesCreatorPresenter {

    void onDestroy();
    void postQuote(Quote quote);
    String getBgImgName();
    String getFontPath();
}
