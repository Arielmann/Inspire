package inspire.ariel.inspire.common.treatslist.view.optionsmenufragment;

import android.graphics.drawable.Drawable;
import android.view.View;

import inspire.ariel.inspire.common.treatslist.view.TreatsListView;

public interface TreatListMenuView {

    void init(TreatsListView treatsListView);

    void resetLoginLogoutBtn(Drawable imgDrawable, View.OnClickListener newListener);

    View.OnClickListener getOnLogoutClicked();

}
