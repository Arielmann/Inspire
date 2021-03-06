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
import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapter;
import inspire.ariel.inspire.common.utils.listutils.DiscreteScrollViewData;

@Module
public class RecyclerViewsModule {


    @Provides
    @Singleton
    TreatListAdapter providesTreatListAdapter() {
        return new TreatListAdapter();
    }

    @Provides
    @Singleton
    @Named(AppStrings.TREAT_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData provideTreatListActivityDiscreteScrollViewData(){
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
    @Named(AppStrings.TREAT_DESIGNER_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA)
    DiscreteScrollViewData provideTreatCreatorActivityDiscreteScrollViewData(){
        return DiscreteScrollViewData.builder()
                .hasFixedSize(true)
                .setSlideOnFling(false)
                .offScreenItems(AppNumbers.TREAT_CREATOR_BG_PICKER_OFF_SCREEN_ITEMS)
                .orientation(Orientation.VERTICAL)
                .timeMillis(AppTimeMillis.QUARTER_SECOND)
                .scaleTransformer(new ScaleTransformer.Builder().setMinScale(Percentages.EIGHTY).build())
                .build();
    }

    @Provides
    @Singleton
    int provideVhTreatLayout(){
        return R.layout.vh_treat_bg_img;
    }
}



