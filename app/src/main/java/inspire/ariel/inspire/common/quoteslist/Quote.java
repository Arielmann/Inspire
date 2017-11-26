package inspire.ariel.inspire.common.quoteslist;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(exclude={"image"})
public class Quote {

    @NonNull private String text;
    @NonNull private int textColor;
    @NonNull private int textSize;
    @NonNull private String fontPath;
    @NonNull private String bgImageName;
    private Drawable image;
    //String bgUrl;

    public static Quote newNoQuotesToPresentQuote(Resources res){
        return Quote.builder().text(res.getString(R.string.error_no_quotes))
                .fontPath(FontsManager.Font.ALEF_BOLD.getPath())
                .bgImageName(AppStrings.BLUE_YELLOW_BG)
                .textSize(Math.round(res.getDimension(R.dimen.error_msg_text_size)))
                .textColor(Color.WHITE)
                .build();
    }
}




