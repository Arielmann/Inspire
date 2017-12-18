package inspire.ariel.inspire.common.di;

import android.support.v4.app.NotificationCompat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.treatslist.model.TreatListModel;
import inspire.ariel.inspire.common.treatslist.model.TreatsListModelImpl;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.owner.treatcreator.model.TreatCreatorModel;
import inspire.ariel.inspire.owner.treatcreator.model.TreatCreatorModelImpl;

@Module
public class ModelsModule {

    private InspireApplication application;

    public ModelsModule(InspireApplication application) {
        this.application = application;
    }

    @Singleton
    @Provides
    TreatListModel provideTreatsListModel(){
        return TreatsListModelImpl.getInstance(application.getAppComponent());
    }

    @Singleton
    @Provides
    TreatCreatorModel provideTreatsCreatorModel(){
        return TreatCreatorModelImpl.builder().bgImageName(AppStrings.BLUE_YELLOW_BG).fontPath(FontsManager.Font.ALEF_BOLD.getPath()).bgDrawableIntValue(R.drawable.blue_yellow_bg).build();
    }
}
