package inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;

public class TreatEditorActivity extends AbstractTreatDesignerActivity {

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
        this.getAbstractTreatDesignerBinding().treatEditText.setText(oldTreat.getText());
        this.getAbstractTreatDesignerBinding().treatEditText.setTextColor(oldTreat.getTextColor());
        this.getAbstractTreatDesignerBinding().treatEditText.setHintTextColor(oldTreat.getTextColor());
        this.getAbstractTreatDesignerBinding().treatEditText.setTextSize(oldTreat.getTextSize());
        this.getAbstractTreatDesignerBinding().designerLayout.setBackground(oldTreat.getImage());
        FontsManager.getInstance().setFontOnTV(oldTreat.getFontPath(), this.getAbstractTreatDesignerBinding().treatEditText);
        presenter.setFontPath(oldTreat.getFontPath());
        this.getAbstractTreatDesignerBinding().bgPicker.scrollToPosition(presenter.getImagePositionFromImageName(oldTreat.getBgImageName()));
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
