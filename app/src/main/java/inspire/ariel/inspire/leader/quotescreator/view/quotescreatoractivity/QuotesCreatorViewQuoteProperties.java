package inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity;

import android.graphics.drawable.Drawable;

import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;

public interface QuotesCreatorViewQuoteProperties {

    void refreshCurrentBackground();

    void setQuoteFont(FontsManager.Font font);

    void setQuoteTextSize(int size);

    void setBackground(Drawable drawable);

    void setQuoteTextColor(int color);

    QuotesCreatorPresenter getPresenter();
}
