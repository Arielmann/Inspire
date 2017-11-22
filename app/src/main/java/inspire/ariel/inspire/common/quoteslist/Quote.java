package inspire.ariel.inspire.common.quoteslist;

import android.graphics.drawable.Drawable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Quote {

    @NonNull private String text;
    @NonNull private int textColor;
    @NonNull private int textSize;
    @NonNull private String fontPath;
    @NonNull private String bgImageName;
    private Drawable image;
    //String bgUrl;
}




