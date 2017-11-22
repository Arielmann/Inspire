package inspire.ariel.inspire.common.resources;

import android.content.res.Resources;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.imageutils.InspireBackgroundImage;

public class ResourcesProviderImpl implements ResourcesProvider, ResourcesInitializer {

    /*
    * This singleton model manages the data related to
    * the creation of quotes.
    */

    @Inject
    @Named(AppStrings.BG_IMAGES)
    List<InspireBackgroundImage> bgImages;

    @Inject
    @Named(AppStrings.COLORS)
    List<Integer> colors;

    @Inject
    @Named(AppStrings.FONTS)
    List<FontsManager.Font> fonts;

    @Inject
    @Named(AppStrings.FONT_SIZES)
    List<ResourcesModule.Size> fontSizes;

    @Inject
    Resources res;

    private static ResourcesProviderImpl model;

    public static ResourcesProviderImpl getInstance() {
        if (model == null) {
            model = new ResourcesProviderImpl();
        }
        return model;
    }

    private ResourcesProviderImpl() {
    }

    @Override
    public void init(InspireApplication application) {
        application.getAppComponent().inject(this);
    }

    @Override
    public List<InspireBackgroundImage> getBackgroundImages() {
        return bgImages;
    }

    @Override
    public List<Integer> getColors() {
        return colors;
    }

    @Override
    public List<FontsManager.Font> getFonts() {
        return fonts;
    }

    @Override
    public List<ResourcesModule.Size> getFontsSizes() {
        return fontSizes;
    }

    @Override
    public Resources getResources() {
        return res;
    }
}



