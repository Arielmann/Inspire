package inspire.ariel.inspire.common.treatslist.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.singletreat.SingleTreatActivity;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.treatslist.events.OnPurchaseClickListener;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.databinding.VhAdminTreatOptionsBinding;
import inspire.ariel.inspire.databinding.VhTreatBinding;

class TreatVH extends RecyclerView.ViewHolder {


    /**
     * Treat possible statuses:
     * 1. Purchaseable - show green purchase button
     * 2. Not purchaseable - hide purchase button
     * 3. Sold out - show gray purchase button
     * 4. Invisible - hide from user
     */


    private static final String TAG = TreatVH.class.getSimpleName();

    VhTreatBinding treatBinding;
    VhAdminTreatOptionsBinding treatAdminOptionsBinding;
    private OnPurchaseClickListener onPurchaseClickListener;

    public TreatVH(VhTreatBinding treatBinding, VhAdminTreatOptionsBinding treatAdminOptionsBinding, OnPurchaseClickListener onPurchaseClickListener) {
        super(treatBinding.getRoot());
        this.treatBinding = treatBinding;
        this.treatAdminOptionsBinding = treatAdminOptionsBinding;
        this.onPurchaseClickListener = onPurchaseClickListener;
        this.treatBinding.treatTv.addEllipsizeListener(ellipsized -> {
            if (ellipsized) {
                this.treatBinding.continueReadBtn.setVisibility(View.VISIBLE);
            } else {
                this.treatBinding.continueReadBtn.setVisibility(View.GONE);
            }
        });
    }

    public void setUIDataOnView(Treat treat, int position) {
        initUserStatusDataBasedViews();
        initTreatStateBasedViews(treat);
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

        treatBinding.optionsManagerBtn.setOnClickListener((View view) -> {
            TreatOptionsVH treatOptions = new TreatOptionsVH(treatAdminOptionsBinding);
            DialogPlus dialog = DialogPlus.newDialog(treatBinding.getRoot().getContext())
                    .setContentHolder(treatOptions)
                    .setExpanded(true, treatOptions.getHeight())
                    .setGravity(Gravity.BOTTOM)
                    .create();
            dialog.show();
            treatOptions.init(dialog, treat, position); //Must call after dialog has shown
        });

        treatBinding.purchaseBtn.setOnClickListener(view -> onPurchaseClickListener.onClick(treat, position));
        Log.d(TAG, treat.getText() + " is presented in the list as a treat");
    }

    private void initUserStatusDataBasedViews() {
        treatBinding.optionsManagerBtn.setVisibility(DataManager.getInstance().getUserStatusData().getTreatOptionsManagerVisibility());
        treatBinding.purchaseBtn.setClickable(DataManager.getInstance().getUserStatusData().isPurchaseBtnClickable());
        treatBinding.purchaseBtn.setBackgroundColor(DataManager.getInstance().getUserStatusData().getPurchaseBtnColor());
    }

    private void initTreatStateBasedViews(Treat treat) {
        if (treat.getUserPurchases() >= treat.getPurchasesLimit()) {
            treatBinding.purchaseBtn.setBackgroundColor(Color.GRAY);
            treatBinding.purchaseBtn.setClickable(false);
            treatBinding.purchaseBtn.setText(treatBinding.getRoot().getResources().getString(R.string.sold_out));
        }
    }
}


