package inspire.ariel.inspire.owner.treatdesigner.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode @ToString
public class TreatDesignerModelImpl implements TreatDesignerModel {

   @NonNull String fontPath;
   @NonNull String bgImageName;
   int bgDrawableIntValue;

}
