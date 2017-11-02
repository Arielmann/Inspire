package inspire.ariel.inspire.common.di;


import javax.inject.Singleton;

import dagger.Component;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;

@Component(modules={AppModule.class, NetworkModule.class, PresentersModule.class, ModelsModule.class})
@Singleton
public interface AppComponent {

    void inject(QuoteListPresenterImpl quotesListPresenter);
}
