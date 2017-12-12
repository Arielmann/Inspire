package inspire.ariel.inspire.owner.treatcreator.view.optionmenufragment;

import android.content.res.AssetManager;

import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorViewController;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorViewTreatProps;

public interface TreatCreatorMenuView {

    void initView();

    void setTreatsCreatorActivityViewProps(TreatsCreatorViewTreatProps treatCreatorViewProps);

    void collapseAllOptionLayouts();

    boolean areAllOptionLayoutsCollapsed();

    void willInject(AppComponent component, TreatsCreatorViewController controller, AssetManager assetManager);
}
