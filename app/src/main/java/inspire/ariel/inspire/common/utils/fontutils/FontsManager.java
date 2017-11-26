package inspire.ariel.inspire.common.utils.fontutils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import javax.inject.Inject;

import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import lombok.Getter;

public class FontsManager  {

    private static final String TAG = FontsManager.class.getSimpleName();
    @Inject
    AssetManager assetManager;

    private static FontsManager manager;

    public static FontsManager getInstance() {
        if (manager == null) {
            manager = new FontsManager();
        }
        return manager;
    }

    private FontsManager() {
    }

    public void init(InspireApplication application) {
        application.getAppComponent().inject(this);
    }

    public enum Font {

        ARIAL(AppStrings.ARIAL, AppStrings.ARIAL_PATH),
        ALEF_REG(AppStrings.ALEF_REG, AppStrings.ALEF_REG_PATH),
        ALEF_BOLD(AppStrings.ALEF_BOLD, AppStrings.ALEF_BOLD_PATH),
        MONTSERRAT_BOLD(AppStrings.MONTSERRAT_BOLD, AppStrings.MONTSERRAT_BOLD_PATH),
        MONTSERRAT_REG(AppStrings.MONTSERRAT_REG, AppStrings.MONTSERRAT_REG_PATH),
        QUIRLYCUES(AppStrings.QUIRLYCUES, AppStrings.QUIRLYCUES_PATH),
        MYRIAD_PRO_REG(AppStrings.MYRIAD_PRO_REG, AppStrings.MYRIAD_PRO_REG_PATH),
        LATO_REGULAR(AppStrings.LARO_REG, AppStrings.LATO_REG_PATH);

        @Getter private final String name;
        @Getter private final String path;

        Font(final String name, final String path) {
            this.name = name;
            this.path = path;
        }
    }

    public void setFontOnTV(Font font, TextView tv) {
        Typeface headlineFont = Typeface.createFromAsset(assetManager, font.path);
        tv.setTypeface(headlineFont);
    }

    public void setFontOnTV(String fontPath, TextView tv) {

        try {
            tv.setTypeface(Typeface.createFromAsset(assetManager, fontPath));
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e(TAG, "Error assigning font with path: " + fontPath + " assigning default font (Alef Bold)");
            tv.setTypeface(Typeface.createFromAsset(assetManager, Font.ALEF_BOLD.path));
        }
    }
}
