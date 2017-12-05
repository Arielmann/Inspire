package inspire.ariel.inspire.leader.quotescreator.view;

import android.graphics.drawable.Drawable;

import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

public interface QuotesCreatorViewForFragments {

    void refreshCurrentBackground();

    void setQuoteFont(FontsManager.Font font);

    void setQuoteTextSize(int size);

    void setBackground(Drawable drawable);

    void setQuoteTextColor(int color);
}
