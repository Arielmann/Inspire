package inspire.ariel.inspire.common.di;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.imageutils.InspireBackgroundImage;
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
    @Named(AppStrings.KEY_BG_IMAGES)
    List<InspireBackgroundImage> provideDefaultBackgroundBitmapsArray() {
        List<InspireBackgroundImage> bgImages = new ArrayList<>();
        bgImages.add(new InspireBackgroundImage(AppStrings.BLUE_YELLOW_BG, res.getDrawable(R.drawable.blue_yellow_bg), R.drawable.blue_yellow_bg));
        bgImages.add(new InspireBackgroundImage(AppStrings.PINK_BG, res.getDrawable(R.drawable.pink_bg), R.drawable.pink_bg));
        bgImages.add(new InspireBackgroundImage(AppStrings.PINK_GREY_BG, res.getDrawable(R.drawable.pink_grey_bg), R.drawable.pink_grey_bg));
        bgImages.add(new InspireBackgroundImage(AppStrings.PURPLE_BLUE_YELLOW_BG, res.getDrawable(R.drawable.purple_blue_yellow_bg), R.drawable.purple_blue_yellow_bg));
        return bgImages;
    }

    @Singleton
    @Provides
    @Named(AppStrings.KEY_BG_IMAGES)
    HashSet<InspireBackgroundImage> provideDefaultBackgroundBitmapsHashMap() {
        HashSet<InspireBackgroundImage> bgImages = new HashSet<>();
        return bgImages;
    }

    @Singleton
    @Provides
    @Named(AppStrings.KEY_COLORS)
    List<Integer> provideColorBitmaps() {
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.YELLOW);
        colors.add(Color.GREEN);
        colors.add(Color.BLACK);
        colors.add(Color.WHITE);
        colors.add(Color.GRAY);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
     /* colors.add(res.getColor(R.color.md_green_900));
        colors.add(res.getColor(R.color.md_brown_700));
        colors.add(res.getColor(R.color.md_grey_700));
        colors.add(res.getColor(R.color.md_deep_purple_700));
        colors.add(res.getColor(R.color.md_pink_300));*/
        return colors;
    }

    @Singleton
    @Provides
    @Named(AppStrings.KEY_FONTS)
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
    @Named(AppStrings.KEY_FONT_SIZES)
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
