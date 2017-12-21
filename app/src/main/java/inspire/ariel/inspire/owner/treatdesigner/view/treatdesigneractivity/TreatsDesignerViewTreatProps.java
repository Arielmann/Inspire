package inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity;

import android.graphics.drawable.Drawable;

import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.owner.treatdesigner.presenter.TreatsDesignerPresenter;

public interface TreatsDesignerViewTreatProps {

    void refreshCurrentBackground();

    void setTreatFont(FontsManager.Font font);

    void setTreatTextSize(int size);

    void setBackground(Drawable drawable);

    void setTreatTextColor(int color);

    TreatsDesignerPresenter getPresenter();
}
