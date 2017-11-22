package inspire.ariel.inspire.common.resources;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.SparseArray;

import java.util.List;

import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.imageutils.InspireBackgroundImage;

public interface ResourcesProvider {
    List<InspireBackgroundImage> getBackgroundImages();
    List<Integer> getColors();
    List<FontsManager.Font> getFonts();
    List<ResourcesModule.Size> getFontsSizes();
    Resources getResources();
}
