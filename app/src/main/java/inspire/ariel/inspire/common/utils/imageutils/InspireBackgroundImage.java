package inspire.ariel.inspire.common.utils.imageutils;

import android.graphics.drawable.Drawable;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InspireBackgroundImage {

   @NonNull private String name;
   @NonNull private Drawable drawable;
   final private int drawableIntValue;
}


