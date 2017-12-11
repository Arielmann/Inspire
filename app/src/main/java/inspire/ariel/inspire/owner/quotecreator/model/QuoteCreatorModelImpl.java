package inspire.ariel.inspire.owner.quotecreator.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode @ToString
public class QuoteCreatorModelImpl implements QuoteCreatorModel{

   @NonNull String fontPath;
   @NonNull String bgImageName;
   int bgDrawableIntValue;

}