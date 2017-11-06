package inspire.ariel.inspire.common.di;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;

@Module
public class ResourcesModule {

    private Resources res;

    public ResourcesModule(Resources res) {
        this.res = res;
    }

    @Singleton
    @Provides
    List<Bitmap> provideDefaultBackgroundImages(){
        List<Bitmap> bgImages = new ArrayList<>();
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg1));
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg2));
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg3));
        bgImages.add(BitmapFactory.decodeResource(res, R.drawable.bg4));
        return bgImages;
    }

    @Singleton
    @Provides
    Resources provideResources(){
        return res;
    }
}
