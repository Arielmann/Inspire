package inspire.ariel.inspire.common.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.treatslist.model.TreatListModel;
import inspire.ariel.inspire.common.treatslist.model.TreatsListModelImpl;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.owner.treatdesigner.model.TreatDesignerModel;
import inspire.ariel.inspire.owner.treatdesigner.model.TreatDesignerModelImpl;

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
    TreatDesignerModel provideTreatsCreatorModel(){
        return TreatDesignerModelImpl.builder().bgImageName(AppStrings.BLUE_YELLOW_BG).fontPath(FontsManager.Font.ALEF_BOLD.getPath()).bgDrawableIntValue(R.drawable.blue_yellow_bg).build();
    }
}
