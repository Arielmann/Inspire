package inspire.ariel.inspire.owner.treatdesigner.model;

import inspire.ariel.inspire.common.localdbmanager.RealmManager;
import inspire.ariel.inspire.common.Treat;
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

   @Override
   public void insertTreatToDb(Treat treat) {
      RealmManager.getInstance().insertTreat(treat);
   }
}
