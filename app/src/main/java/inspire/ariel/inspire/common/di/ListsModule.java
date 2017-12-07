package inspire.ariel.inspire.common.di;

import com.yarolegovich.discretescrollview.Orientation;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.constants.Percentages;
import inspire.ariel.inspire.common.quoteslist.adapter.QuoteListAdapter;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;

@Module
public class ListsModule {


    @Provides
    @Singleton
    QuoteListAdapter providesQuoteListAdapter() {
        return new QuoteListAdapter();
    }

    @Provides
    @Singleton
    @Named(AppStrings.QUOTE_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData provideQuoteListActivityDiscreteScrollViewData(){
        return DiscreteScrollViewData.builder()
                .hasFixedSize(true)
                .setSlideOnFling(true)
                .offScreenItems(AppNumbers.DISCRETE_SCROLL_VIEW_OFF_SCREEN_ITEMS)
                .orientation(Orientation.VERTICAL)
                .timeMillis(AppTimeMillis.QUARTER_SECOND)
                .scaleTransformer(new ScaleTransformer.Builder().setMinScale(Percentages.EIGHTY).build())
                .build();
    }

    @Provides
    @Singleton
    @Named(AppStrings.QUOTE_CREATOR_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData provideQuoteCreatorActivityDiscreteScrollViewData(){
        return DiscreteScrollViewData.builder()
                .hasFixedSize(true)
                .setSlideOnFling(false)
                .offScreenItems(AppNumbers.QUOTE_CREATOR_BG_PICKER_OFF_SCREEN_ITEMS)
                .orientation(Orientation.VERTICAL)
                .timeMillis(AppTimeMillis.QUARTER_SECOND)
                .scaleTransformer(new ScaleTransformer.Builder().setMinScale(Percentages.EIGHTY).build())
                .build();
    }

    @Provides
    @Singleton
    int provideVhQuoteLayout(){
        return R.layout.vh_quote_bg_img;
    }
}



