package inspire.ariel.inspire.common.di;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.facebook.CallbackManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.utils.backendutils.NetworkChecker;

@Module
public class NetworkModule {

    @Singleton
    @Provides
    @Named(AppStrings.BACKENDLESS_TABLE_USERS)
    IDataStore<BackendlessUser> provideLeadersStorage() {
        return Backendless.Data.of(BackendlessUser.class);
    }

    @Singleton
    @Provides
    @Named(AppStrings.BACKENDLESS_TABLE_TREATS)
    IDataStore<Treat> provideTreatsStorage() {
        return Backendless.Data.of(Treat.class);
    }

    @Singleton
    @Provides
    DataQueryBuilder providePagedTreatsQueryBuilder() {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy(AppStrings.BACKENDLESS_SORT_CLAUSE_CREATED_DSC);
        queryBuilder.setWhereClause(AppStrings.BACKENDLESS_OWNER_ID_AND_VISIBLE_WHERE_CLAUSE);
        queryBuilder.setPageSize(AppNumbers.TREAT_QUERY_PAGE_SIZE).setOffset(AppNumbers.TREAT_QUERY_STARTING_OFFSET);
        return queryBuilder;
    }

    @Singleton
    @Provides
    LoadRelationsQueryBuilder<Treat> providePagedTreatsRelationQueryBuilder() {
        LoadRelationsQueryBuilder<Treat> loadRelationsQueryBuilder;
        loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of(Treat.class);
        loadRelationsQueryBuilder.setRelationName( AppStrings.BACKENDLESS_TABLE_USER_COLUMN_PURCHASED_TREATS );
        loadRelationsQueryBuilder.setPageSize(AppNumbers.PURCHASED_TREAT_QUERY_PAGE_SIZE).setOffset(AppNumbers.TREAT_QUERY_STARTING_OFFSET);
        return loadRelationsQueryBuilder;
    }

    @Provides
    @Singleton
    NetworkChecker provideNetworkChecker() {
        return NetworkChecker.getInstance();
    }

    @Provides
    @Singleton
    CallbackManager provideFbCallbackManager() {
        return CallbackManager.Factory.create();
    }
}
