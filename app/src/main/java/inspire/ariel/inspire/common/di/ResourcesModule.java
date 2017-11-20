package inspire.ariel.inspire.common.di;

import android.content.res.AssetManager;
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
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.listutils.ListPresentable;
import lombok.Getter;

@Module
public class ResourcesModule {

    public enum Size {

        SIXTEEN(AppInts.SIXTEEN),
        TWENTY(AppInts.TWENTY),
        THIRTY(AppInts.THIRTY),
        FORTY(AppInts.FORTY),
        FIFTY(AppInts.FIFTY),
        SIXTY(AppInts.SIXTY),
        SEVENTY(AppInts.SEVENTY);

        @Getter private final int size;

        Size(final int name) {
            this.size = name;
        }
    }

    private Resources res;
    private AssetManager assetManager;

    public ResourcesModule(Resources res, AssetManager assetManager) {
        this.res = res;
        this.assetManager = assetManager;
    }

    @Singleton
    @Provides
    @Named(AppStrings.BG_IMAGES)
    List<Bitmap> provideDefaultBackgroundBitmaps() {
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
    List<Integer> provideColorBitmaps() {
        List<Integer> colors = new ArrayList<>();
        colors.add(res.getColor(R.color.md_red_700));
        colors.add(res.getColor(R.color.md_blue_700));
        colors.add(res.getColor(R.color.md_yellow_300));
        colors.add(res.getColor(R.color.md_black_1000));
        colors.add(res.getColor(R.color.md_white_1000));
        colors.add(res.getColor(R.color.md_orange_300));
        colors.add(res.getColor(R.color.md_teal_300));
        colors.add(res.getColor(R.color.md_light_green_400));
        colors.add(res.getColor(R.color.md_green_900));
        colors.add(res.getColor(R.color.md_brown_700));
        colors.add(res.getColor(R.color.md_grey_700));
        colors.add(res.getColor(R.color.md_deep_purple_700));
        colors.add(res.getColor(R.color.md_pink_300));
        return colors;
    }

    @Singleton
    @Provides
    @Named(AppStrings.FONTS)
    List<FontsManager.Font> provideFonts() {
        List<FontsManager.Font> fonts = new ArrayList<>();
        fonts.add(FontsManager.Font.ARIAL);
        fonts.add(FontsManager.Font.ALEF_REG);
        fonts.add(FontsManager.Font.ALEF_BOLD);
        fonts.add(FontsManager.Font.LATO_REGULAR);
        fonts.add(FontsManager.Font.MONTSERRAT_BOLD);
        fonts.add(FontsManager.Font.MONTSERRAT_REG);
        fonts.add(FontsManager.Font.MYRIAD_PRO_REG);
        fonts.add(FontsManager.Font.QUIRLYCUES);
        return fonts;
    }

    @Singleton
    @Provides
    @Named(AppStrings.FONT_SIZES)
    List<Size> provideFontsSizes() {
        List<Size> sizes = new ArrayList<>();
        sizes.add(Size.SIXTEEN);
        sizes.add(Size.TWENTY);
        sizes.add(Size.THIRTY);
        sizes.add(Size.FORTY);
        sizes.add(Size.FIFTY);
        sizes.add(Size.SIXTY);
        sizes.add(Size.SEVENTY);
        return sizes;
    }

    @Singleton
    @Provides
    Resources provideResources() {
        return res;
    }

    @Singleton
    @Provides
    AssetManager provideAssetManager() {return assetManager;}
}
