package inspire.ariel.inspire.common.utils.imageutils;

import android.graphics.drawable.Drawable;

import lombok.Data;
import lombok.NonNull;

@Data
public class InspireBackgroundImage {

   @NonNull String name;
   @NonNull Drawable drawable;

   public InspireBackgroundImage(String identifier, Drawable drawable) {
      this.name = identifier;
      this.drawable = drawable;
   }
}


