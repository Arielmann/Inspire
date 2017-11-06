package inspire.ariel.inspire.common.di;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.persistence.DataQueryBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.leader.Leader;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;

@Module
public class NetworkModule {

    public NetworkModule() {

    }

    @Singleton
    @Provides
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> provideActionGroups() {
        return Backendless.Data.of(Quote.class);
    }

    @Singleton
    @Provides
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> provideLeadersStorage() {
        return Backendless.Data.of(Leader.class);
    }

    @Singleton
    @Provides
    DataQueryBuilder provideDataQueryBuilder() {
        return DataQueryBuilder.create();
    }


  /*  @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }*/

    @Provides
    @Singleton
    NetworkHelper provideNetworkHelper() {
        return NetworkHelper.getInstance();
    }
}
