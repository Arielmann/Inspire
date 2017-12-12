package inspire.ariel.inspire.owner.treatcreator.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode @ToString
public class TreatCreatorModelImpl implements TreatCreatorModel {

   @NonNull String fontPath;
   @NonNull String bgImageName;
   int bgDrawableIntValue;

}
