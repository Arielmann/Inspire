package inspire.ariel.inspire.common.utils.imageutils;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.InputStream;

import inspire.ariel.inspire.R;

public class ImageUtils {

    public static Drawable createDrawableFromUri(Uri uri, ContentResolver contentResolver, Resources res) {
        Drawable drawable;
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            drawable = Drawable.createFromStream(inputStream, uri.toString());
        } catch (FileNotFoundException e) {
            drawable = res.getDrawable(R.drawable.blue_yellow_bg);
        }

        return drawable;
    }
}
