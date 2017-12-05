package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.model.QuoteListModel;
import inspire.ariel.inspire.common.quoteslist.model.QuotesListModelImpl;
import inspire.ariel.inspire.common.resources.ResourcesInitializer;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.resources.ResourcesProviderImpl;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.leader.quotescreator.model.QuoteCreatorModel;
import inspire.ariel.inspire.leader.quotescreator.model.QuoteCreatorModelImpl;

@Module
public class ModelsModule {

    @Singleton
    @Provides
    QuoteListModel provideQuotesListModel(){
        return QuotesListModelImpl.getInstance();
    }

    @Singleton
    @Provides
    QuoteCreatorModel provideQuotesCreatorModel(){
        return QuoteCreatorModelImpl.builder().bgImageName(AppStrings.BLUE_YELLOW_BG).fontPath(FontsManager.Font.ALEF_BOLD.getPath()).bgDrawableIntValue(R.drawable.blue_yellow_bg).build();
    }
}
