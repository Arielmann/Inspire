package inspire.ariel.inspire.owner.treatcreator.presenter;

import android.graphics.drawable.Drawable;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.treatslist.Treat;

public interface TreatsCreatorPresenter {

    void onDestroy();

    void requestPostTreat(Treat treat);

    String getBgImgName();

    String getFontPath();

    Drawable getChosenBgImage();

    void requestUpdateTreat(Treat treat, int position);

    void onTreatFontClicked(String path);

    DiscreteScrollView.OnItemChangedListener<?> getOnItemChangedListener();

    void setFontPath(String path);

    int getImagePositionFromImageName(String bgImageName);
}