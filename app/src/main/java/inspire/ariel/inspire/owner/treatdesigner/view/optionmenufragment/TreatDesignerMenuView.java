package inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment;

import android.content.res.AssetManager;

import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.TreatsDesignerViewController;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.TreatsDesignerViewTreatProps;

public interface TreatDesignerMenuView {

    void init();

    void setTreatsDesignerActivityViewProps(TreatsDesignerViewTreatProps treatCreatorViewProps);

    void collapseAllOptionLayouts();

    boolean areAllOptionLayoutsCollapsed();

    void willInject(AppComponent component, TreatsDesignerViewController controller, AssetManager assetManager);
}
