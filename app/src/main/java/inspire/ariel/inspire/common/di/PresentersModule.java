package inspire.ariel.inspire.common.di;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.loginactivity.presenter.LoginPresenter;
import inspire.ariel.inspire.common.loginactivity.presenter.LoginPresenterImpl;
import inspire.ariel.inspire.common.loginactivity.view.LoginView;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenter;
import inspire.ariel.inspire.common.treatslist.presenter.TreatsListPresenterImpl;
import inspire.ariel.inspire.common.treatslist.view.TreatsListView;
import inspire.ariel.inspire.owner.treatdesigner.presenter.TreatsDesignerPresenter;
import inspire.ariel.inspire.owner.treatdesigner.presenter.TreatsDesignerPresenterImpl;
import inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity.TreatsDesignerViewController;
import lombok.Builder;
import lombok.NonNull;

@Module
@Builder
public class PresentersModule {

    @NonNull private AppComponent appComponent;
    private final TreatsDesignerViewController treatsDesignerViewController;
    private final TreatsListView treatsListView;
    private final LoginView loginView;

    public PresentersModule(AppComponent appComponent, TreatsDesignerViewController treatsDesignerViewController, TreatsListView treatsListView, LoginView loginView) {
        this.appComponent = appComponent;
        this.treatsDesignerViewController = treatsDesignerViewController;
        this.treatsListView = treatsListView;
        this.loginView = loginView;
    }

    @Provides
    public TreatsDesignerPresenter provideTreatDesignerPresenter(){
        return new TreatsDesignerPresenterImpl(appComponent, treatsDesignerViewController);
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
