package inspire.ariel.inspire.leader.quotescreator.model;

import inspire.ariel.inspire.R;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@EqualsAndHashCode @ToString
public class QuoteCreatorModelImpl implements QuoteCreatorModel{

   @NonNull String fontPath;
   @NonNull String bgImageName;
   @NonNull int bgDrawableIntValue;

}
