package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Component;
import inspire.ariel.inspire.common.loginactivity.view.LoginActivity;
import inspire.ariel.inspire.common.drawer.DrawerFragment;
import inspire.ariel.inspire.common.treatslist.view.TreatsListActivity;
import inspire.ariel.inspire.owner.treatdesigner.view.optionmenufragment.TreatDesignerMenuFragment;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.AbstractTreatDesignerActivity;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.TreatsCreatorActivity;

@Component(modules = {AppModule.class, ViewsModule.class, ResourcesModule.class, RecyclerViewsModule.class, PresentersModule.class})
@Singleton
public interface ViewComponent {

    void inject(TreatDesignerMenuFragment treatDesignerMenuFragment);

    void inject(TreatsCreatorActivity treatsCreatorActivity);

    void inject(TreatsListActivity treatsListActivity);

    void inject(LoginActivity loginActivity);

    void inject(AbstractTreatDesignerActivity abstractTreatDesignerActivity);

}
