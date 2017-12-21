package inspire.ariel.inspire.common.di;

import android.content.Context;
import android.content.res.Resources;

import inspire.ariel.inspire.databinding.ActivityAbstractTreatDesignerBinding;

public interface ViewInjector {

    Context getContext();

    android.support.v4.app.FragmentManager getSupportFragmentManager();

    Resources getResources();

    default ActivityAbstractTreatDesignerBinding getAbstractTreatDesignerBinding(){return null;}

}
