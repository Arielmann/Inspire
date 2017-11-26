package inspire.ariel.inspire.leader.quotescreator.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

public interface QuotesCreatorView {

    void setBackground(Drawable newBackground);
    void setQuoteTextSize(int size);
    void setQuoteFont(FontsManager.Font font);
    void setQuoteTextColor(int color);
    void showUploadErrorMessage(String message);
    void goToQuoteListActivity();
    void showProgressDialog();

    DiscreteScrollView getBgPicker();
    Resources getResources();
    Context getContext();


}
