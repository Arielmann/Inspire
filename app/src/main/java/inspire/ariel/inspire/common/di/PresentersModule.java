package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapterPresenter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenter;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenterImpl;
import inspire.ariel.inspire.leader.quotescreator.view.quotescreatoractivity.QuotesCreatorViewController;
import lombok.Builder;
import lombok.NonNull;

@Module
@Builder
public class PresentersModule {

    @NonNull private AppComponent appComponent;
    private QuotesCreatorViewController quotesCreatorViewController;
    private QuotesListView quotesListView;

    public PresentersModule(AppComponent appComponent, QuotesCreatorViewController quotesCreatorViewController, QuotesListView quotesListView) {
        this.appComponent = appComponent;
        this.quotesCreatorViewController = quotesCreatorViewController;
        this.quotesListView = quotesListView;
    }

    @Provides
    @Singleton
    public QuotesCreatorPresenter provideQuoteCreatorPresenter(){
        return new QuotesCreatorPresenterImpl(appComponent, quotesCreatorViewController);
    }

    @Provides
    @Singleton
    public QuoteListPresenter provideQuoteListPresenter(){
        return new QuoteListPresenterImpl(appComponent, quotesListView);
    }
}
