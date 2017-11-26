package inspire.ariel.inspire.common.utils.imageutils;

import android.graphics.drawable.Drawable;

import lombok.Data;
import lombok.NonNull;

@Data
public class InspireBackgroundImage {

   @NonNull private String name;
   @NonNull private Drawable drawable;
   @NonNull private int drawableIntValue;
}


