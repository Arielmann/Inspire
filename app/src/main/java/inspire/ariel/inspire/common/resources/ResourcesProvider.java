package inspire.ariel.inspire.common.resources;

import android.graphics.Bitmap;

import java.util.List;

public interface ResourcesProvider {
    List<Bitmap> getBackgroundImages();
    List<Bitmap> getColors();
    List<String> getFonts();
}
