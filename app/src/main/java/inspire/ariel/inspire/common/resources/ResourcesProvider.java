package inspire.ariel.inspire.common.resources;

import android.graphics.Bitmap;

import java.util.List;

import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

public interface ResourcesProvider {
    List<Bitmap> getBackgroundImages();
    List<Integer> getColors();
    List<FontsManager.Font> getFonts();
    List<ResourcesModule.Size> getFontsSizes();
}
