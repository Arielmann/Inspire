package inspire.ariel.inspire.common.resources;

import android.graphics.Bitmap;

import java.util.List;

import javax.inject.Inject;

import inspire.ariel.inspire.common.app.InspireApplication;

public class ResourcesProviderImpl implements ResourcesProvider, ResourcesInitializer {

    /*
    * This singleton model manages the data related to
    * the creation of quotes.
    */

    @Inject
    List<Bitmap> bgImages;

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
    public List<Bitmap> getBackgroundImages() {
        return bgImages;
    }

    @Override
    public void init(InspireApplication application) {
        application.getAppComponent().inject(this);
    }
}


