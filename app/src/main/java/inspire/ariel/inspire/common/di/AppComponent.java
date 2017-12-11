package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Component;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.quoteslist.services.PushNotificationService;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.resources.ResourcesProviderImpl;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.owner.quotecreator.presenter.QuotesCreatorPresenterImpl;

/**
 * NOTE: For injecting within any class that wants
 * to inject views (Activities, Fragments etc.), use ViewComponent.
 */
@Component(modules = {AppModule.class, NetworkModule.class, ModelsModule.class, ResourcesModule.class, ListsModule.class})
@Singleton
public interface AppComponent {

    void inject(QuoteListPresenterImpl quotesListPresenter);

    void inject(QuotesCreatorPresenterImpl quotesCreatorPresenter);

    void inject(ResourcesProviderImpl resourceProvider);

    void inject(FontsManager fontsManager);

    void inject(InspireApplication application);

    void inject(PushNotificationService pushNotificationService);

}
