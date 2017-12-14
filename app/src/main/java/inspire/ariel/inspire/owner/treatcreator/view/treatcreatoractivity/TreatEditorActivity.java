package inspire.ariel.inspire.owner.treatcreator.view.treatcreatoractivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

public class TreatEditorActivity extends TreatsCreatorActivity {

    private Treat oldTreat;
    private int oldTreatPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldTreat = getIntent().getParcelableExtra(AppStrings.KEY_TREAT);
        oldTreatPosition = getIntent().getIntExtra(AppStrings.KEY_TREAT_POSITION, AppNumbers.ERROR_INT);
        initAtEditMode();
    }

    @Override
    void requestTreatPost() {
        Treat newTreat = super.createTreatForPost();
        newTreat.setObjectId(oldTreat.getObjectId());
        presenter.requestUpdateTreat(newTreat, oldTreatPosition);
    }

    private void initAtEditMode() {
        super.getBinding().treatEditText.setText(oldTreat.getText());
        super.getBinding().treatEditText.setTextColor(oldTreat.getTextColor());
        super.getBinding().treatEditText.setHintTextColor(oldTreat.getTextColor());
        super.getBinding().treatEditText.setTextSize(oldTreat.getTextSize());
        super.getBinding().creatorLayout.setBackground(oldTreat.getImage());
        FontsManager.getInstance().setFontOnTV(oldTreat.getFontPath(), super.getBinding().treatEditText);
        presenter.setFontPath(oldTreat.getFontPath());
        getBinding().bgPicker.scrollToPosition(presenter.getImagePositionFromImageName(oldTreat.getBgImageName()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppStrings.SAVED_STATE_OLD_TREAT, oldTreat);
        outState.putInt(AppStrings.SAVED_STATE_OLD_TREAT_POSITION, oldTreatPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            oldTreat = savedInstanceState.getParcelable(AppStrings.SAVED_STATE_OLD_TREAT);
            oldTreatPosition = savedInstanceState.getInt(AppStrings.SAVED_STATE_OLD_TREAT_POSITION);
        }
    }
}