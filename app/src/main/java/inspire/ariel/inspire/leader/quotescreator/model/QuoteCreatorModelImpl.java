package inspire.ariel.inspire.leader.quotescreator.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode @ToString
public class QuoteCreatorModelImpl implements QuoteCreatorModel{

    String fontPath;
    String bgImageName;

    public QuoteCreatorModelImpl(String fontPath, String bgImageName) {
        this.fontPath = fontPath;
        this.bgImageName = bgImageName;
    }

    @Override
    public void setFontPath(String path) {
        this.fontPath = fontPath;
    }

    @Override
    public String getFontPath() {
        return fontPath;
    }

    @Override
    public void setBgImageName(String name) {
        this.bgImageName = name;
    }

    @Override
    public String getBgImageName() {
        return bgImageName;
    }
}
