package inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity;

import android.content.Context;
import android.content.res.Resources;

import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;

public interface QuotesCreatorActivityInjector {

    ActivityQuoteCreatorBinding getBinding();

    Resources getResources();

    android.support.v4.app.FragmentManager getSupportFragmentManager();

    Context getContext();
}
