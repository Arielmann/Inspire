package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenter;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenterImpl;
import inspire.ariel.inspire.common.treatslist.view.TreatsListView;
import inspire.ariel.inspire.owner.treatcreator.presenter.TreatsCreatorPresenter;
import inspire.ariel.inspire.owner.treatcreator.presenter.TreatsCreatorPresenterImpl;
import inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity.TreatsCreatorViewController;
import lombok.Builder;
import lombok.NonNull;

@Module
@Builder
public class PresentersModule {

    @NonNull private AppComponent appComponent;
    private TreatsCreatorViewController treatsCreatorViewController;
    private TreatsListView treatsListView;

    public PresentersModule(AppComponent appComponent, TreatsCreatorViewController treatsCreatorViewController, TreatsListView treatsListView) {
        this.appComponent = appComponent;
        this.treatsCreatorViewController = treatsCreatorViewController;
        this.treatsListView = treatsListView;
    }

    @Provides
    @Singleton
    public TreatsCreatorPresenter provideTreatCreatorPresenter(){
        return new TreatsCreatorPresenterImpl(appComponent, treatsCreatorViewController);
    }

    @Provides
    @Singleton
    public TreatsListPresenter provideTreatListPresenter(){
        return new TreatsListPresenterImpl(appComponent, treatsListView);
    }
}
