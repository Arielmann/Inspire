package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Component;
import inspire.ariel.inspire.common.treatslist.view.TreatsListActivity;
import inspire.ariel.inspire.owner.treatcreator.view.optionmenufragment.TreatCreatorMenuFragment;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorActivity;

@Component(modules = {AppModule.class, ViewsModule.class, ResourcesModule.class, ListsModule.class, PresentersModule.class})
@Singleton
public interface ViewComponent {

    void inject(TreatCreatorMenuFragment treatCreatorMenuFragment);

    void inject(TreatsCreatorActivity treatsCreatorActivity);

    void inject(TreatsListActivity treatsListActivity);
}
