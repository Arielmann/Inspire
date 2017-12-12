package inspire.ariel.inspire.common.utils.viewutils;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

import inspire.ariel.inspire.common.constants.AppNumbers;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProgressDialogFactory {

    private Context context;

    public KProgressHUD newInstance(String label){
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setCancellable(true)
                .setAnimationSpeed(AppNumbers.PROGRESS_DIALOG_ANIM_SPEED)
                .setDimAmount(AppNumbers.PROGRESS_DIALOG_DIM_AMOUNT);
    }

    public KProgressHUD newInstance() {
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(AppNumbers.PROGRESS_DIALOG_ANIM_SPEED)
                .setDimAmount(AppNumbers.PROGRESS_DIALOG_DIM_AMOUNT);
    }
}
