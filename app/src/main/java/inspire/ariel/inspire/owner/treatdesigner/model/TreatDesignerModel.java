package inspire.ariel.inspire.owner.treatdesigner.model;

import inspire.ariel.inspire.common.Treat;

public interface TreatDesignerModel {

    void setFontPath(String path);

    void setBgImageName(String name);

    void setBgDrawableIntValue(int index);

    String getFontPath();

    String getBgImageName();

    int getBgDrawableIntValue();

    void insertTreatToDb(Treat treat);
}
