package inspire.ariel.inspire.common.di;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;

@Module
public class ResourcesModule {

    private Resources res;

    public ResourcesModule(Resources res) {
        this.res = res;
    }

    @Singleton
    @Provides
    @Named(AppStrings.BG_IMAGES)
    List<Bitmap> provideDefaultBackgroundBitmaps(){
        List<Bitmap> bgImages = new ArrayList<>();
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg1));
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg2));
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg3));
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg4));
        return bgImages;
    }

    @Singleton
    @Provides
    @Named(AppStrings.COLORS)
    List<Bitmap> provideColorBitmaps(){
        List<Bitmap> colors = new ArrayList<>();
        colors.add(BitmapFactory.decodeResource(res, R.drawable.bg1));
        colors.add(BitmapFactory.decodeResource(res, R.drawable.bg2));
        colors.add(BitmapFactory.decodeResource(res, R.drawable.bg3));
        colors.add(BitmapFactory.decodeResource(res, R.drawable.bg4));
        return colors;
    }

    @Singleton
    @Provides
    List<String> provideFonts(){
        List<String> fonts = new ArrayList<>();
        fonts.add(AppStrings.ARIAL);
        fonts.add(AppStrings.DAVID);
        fonts.add(AppStrings.VERDANA);
        return fonts;
    }



    @Singleton
    @Provides
    Resources provideResources(){
        return res;
    }
}
