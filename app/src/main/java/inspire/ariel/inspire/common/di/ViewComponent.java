package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Component;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;
import inspire.ariel.inspire.leader.quotescreator.view.optionmenufragment.QuoteCreatorMenuFragment;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorActivity;

@Component(modules = {AppModule.class, ViewsModule.class, ResourcesModule.class, ListsModule.class, PresentersModule.class})
@Singleton
public interface ViewComponent {

    void inject(QuoteCreatorMenuFragment quoteCreatorMenuFragment);

    void inject(QuotesCreatorActivity quotesCreatorActivity);

    void inject(QuotesListActivity quotesListActivity);
}
