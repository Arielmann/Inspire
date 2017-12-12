package inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity;

import android.graphics.drawable.Drawable;

import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.owner.treatcreator.presenter.TreatsCreatorPresenter;

public interface TreatsCreatorViewTreatProps {

    void refreshCurrentBackground();

    void setTreatFont(FontsManager.Font font);

    void setTreatTextSize(int size);

    void setBackground(Drawable drawable);

    void setTreatTextColor(int color);

    TreatsCreatorPresenter getPresenter();
}
