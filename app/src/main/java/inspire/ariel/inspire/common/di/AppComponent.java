package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Component;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.quoteslist.services.PushNotificationService;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;
import inspire.ariel.inspire.common.resources.ResourcesProviderImpl;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenterImpl;
import inspire.ariel.inspire.leader.quotescreator.view.QuoteCreatorMenuFragment;
import inspire.ariel.inspire.leader.quotescreator.view.QuotesCreatorActivity;

@Component(modules = {AppModule.class, NetworkModule.class, ModelsModule.class, ResourcesModule.class, ListsModule.class})
@Singleton
public interface AppComponent {

    void inject(QuoteListPresenterImpl quotesListPresenter);

    void inject(QuotesCreatorPresenterImpl quotesCreatorPresenter);

    void inject(ResourcesProviderImpl resourceProvider);

    void inject(AppNumbers appInts);

    void inject(FontsManager fontsManager);

    void inject(InspireApplication application);

    void inject(PushNotificationService pushNotificationService);

    void inject(QuotesCreatorActivity quotesCreatorActivity);

    void inject(QuoteCreatorMenuFragment quoteCreatorMenuFragment);

    void inject(QuotesListActivity quotesListActivity);
}
