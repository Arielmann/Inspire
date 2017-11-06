package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.model.QuotesListModelImpl;
import inspire.ariel.inspire.common.resources.ResourcesInitializer;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.resources.ResourcesProviderImpl;

@Module
public class ModelsModule {
    @Singleton
    @Provides
    QuoteListModel provideQuotesListModel(){
        return QuotesListModelImpl.getInstance();
    }

    @Singleton
    @Provides
    ResourcesProvider provideResourcesProvider(){
        return ResourcesProviderImpl.getInstance();
    }

    @Singleton
    @Provides
    ResourcesInitializer provideResourcesInitializer(){
        return ResourcesProviderImpl.getInstance();
    }
}
