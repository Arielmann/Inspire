package inspire.ariel.inspire.common.resources;

import android.graphics.Bitmap;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;

public class ResourcesProviderImpl implements ResourcesProvider, ResourcesInitializer {

    /*
    * This singleton model manages the data related to
    * the creation of quotes.
    */

    @Inject
    @Named(AppStrings.BG_IMAGES)
    List<Bitmap> bgImages;

    @Inject
    @Named(AppStrings.COLORS)
    List<Bitmap> colors;

    @Inject
    List<String> fonts;

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
    public List<Bitmap> getBackgroundImages() {
        return bgImages;
    }

    @Override
    public List<Bitmap> getColors() {
        return colors;
    }
    @Override
    public List<String> getFonts() {
        return fonts;
    }
}



