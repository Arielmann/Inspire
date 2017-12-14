package inspire.ariel.inspire.common.treatslist.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.singletreat.SingleTreatActivity;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.databinding.VhTreatBinding;
import inspire.ariel.inspire.databinding.VhTreatOptionsBinding;

class TreatVH extends RecyclerView.ViewHolder {

    private static final String TAG = TreatVH.class.getSimpleName();

    VhTreatBinding treatBinding;
    VhTreatOptionsBinding treatManagerOptionsBinding;

    public TreatVH(VhTreatBinding treatBinding, VhTreatOptionsBinding treatOptionsManagerBinding) {
        super(treatBinding.getRoot());
        this.treatBinding = treatBinding;
        this.treatManagerOptionsBinding = treatOptionsManagerBinding;
        this.treatBinding.treatTv.addEllipsizeListener(ellipsized -> {
            if (ellipsized) {
                this.treatBinding.continueReadBtn.setVisibility(View.VISIBLE);
            } else {
                this.treatBinding.continueReadBtn.setVisibility(View.GONE);
            }
        });
    }

    public void setUIDataOnView(Treat treat, int position) {
        treatBinding.treatTv.setText(treat.getText());
        treatBinding.treatTv.setTextColor(treat.getTextColor());
        treatBinding.treatTv.setTextSize(treat.getTextSize());
        FontsManager.getInstance().setFontOnTV(treat.getFontPath(), treatBinding.treatTv);
        treatBinding.treatLayout.setBackground(treat.getImage());
        treatBinding.continueReadBtn.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), SingleTreatActivity.class);
            intent.putExtra(AppStrings.KEY_TREAT, treat);
            itemView.getContext().startActivity(intent);
        });

        treatBinding.managerOptionsBtn.setOnClickListener((View view) -> {

            TreatOptionsVH treatOptions = new TreatOptionsVH(treatManagerOptionsBinding);
            DialogPlus dialog = DialogPlus.newDialog(treatBinding.getRoot().getContext())
                    .setContentHolder(treatOptions)
                    .setExpanded(true, treatOptions.getHeight())
                    .setGravity(Gravity.BOTTOM)
                    .create();
            dialog.show();

            treatOptions.init(dialog, treat, position); //Must call after dialog has shown
        });

        Log.d(TAG, treat.getText() + " is presented in the list as a treat");
    }


}
