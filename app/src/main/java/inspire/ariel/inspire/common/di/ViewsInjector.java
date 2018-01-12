package inspire.ariel.inspire.common.di;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import inspire.ariel.inspire.databinding.ActivityAbstractTreatDesignerBinding;
import inspire.ariel.inspire.databinding.FragmentDrawerBinding;

public interface ViewsInjector {

    Context getContext();

    default android.support.v4.app.FragmentManager getSupportFragmentManager(){return null;}

    Resources getResources();

    default ActivityAbstractTreatDesignerBinding getAbstractTreatDesignerBinding(){return null;}

    default FragmentDrawerBinding getDrawerFragmentBinding(){return null;}

    default Activity getActivity(){return null;}

}
