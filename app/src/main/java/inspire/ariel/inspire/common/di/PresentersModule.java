package inspire.ariel.inspire.common.di;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.loginactivity.presenter.LoginPresenter;
import inspire.ariel.inspire.common.loginactivity.presenter.LoginPresenterImpl;
import inspire.ariel.inspire.common.loginactivity.view.LoginView;
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
    private LoginView loginView;

    public PresentersModule(AppComponent appComponent, TreatsCreatorViewController treatsCreatorViewController, TreatsListView treatsListView, LoginView loginView) {
        this.appComponent = appComponent;
        this.treatsCreatorViewController = treatsCreatorViewController;
        this.treatsListView = treatsListView;
        this.loginView = loginView;
    }

    @Provides
    public TreatsCreatorPresenter provideTreatCreatorPresenter(){
        return new TreatsCreatorPresenterImpl(appComponent, treatsCreatorViewController);
    }

    @Provides
    public TreatsListPresenter provideTreatListPresenter(){
        return new TreatsListPresenterImpl(appComponent, treatsListView);
    }

    @Provides
    public LoginPresenter provideLoginPresenter(){
        return new LoginPresenterImpl(appComponent, loginView);
    }
}
