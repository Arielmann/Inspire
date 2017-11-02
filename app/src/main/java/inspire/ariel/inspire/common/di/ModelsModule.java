package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.quoteslist.model.QuotesModel;
import inspire.ariel.inspire.common.quoteslist.model.QuotesModelImpl;

@Module
public class ModelsModule {
    @Singleton
    @Provides
    QuotesModel provideQuotesModel(){
        return QuotesModelImpl.getInstance();
    }
}
