package inspire.ariel.inspire.owner.quotecreator.model;

public interface QuoteCreatorModel {

    void setFontPath(String path);

    void setBgImageName(String name);

    void setBgDrawableIntValue(int index);

    String getFontPath();

    String getBgImageName();

    int getBgDrawableIntValue();
}
