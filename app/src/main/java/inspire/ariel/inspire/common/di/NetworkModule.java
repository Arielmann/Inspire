package inspire.ariel.inspire.common.di;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.persistence.DataQueryBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.constants.AppNumbers;
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
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> provideLeadersStorage() {
        return Backendless.Data.of(Leader.class);
    }

    @Singleton
    @Provides
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> provideQuotesStorage() {
        return Backendless.Data.of(Quote.class);
    }

    @Singleton
    @Provides
    DataQueryBuilder provideQuoteRelationsQueryBuilder() {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy(AppStrings.BACKENDLESS_SORT_CLAUSE_CREATED_DSC);
        queryBuilder.setWhereClause(AppStrings.BACKENDLESS_LEADER_ID_WHERE_CLAUSE);
        queryBuilder.setPageSize(AppNumbers.QUOTES_QUERY_PAGE_SIZE).setOffset(AppNumbers.QUOTES_QUERY_STARTING_OFFSET);
        return queryBuilder;
    }

    @Provides
    @Singleton
    NetworkHelper provideNetworkHelper() {
        return NetworkHelper.getInstance();
    }
}
