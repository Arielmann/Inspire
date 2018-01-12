package inspire.ariel.inspire.common.treatslist.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;

import hugo.weaving.DebugLog;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.singletreat.SingleTreatActivity;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.treatslist.events.OnPurchaseClickListener;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.databinding.VhAdminTreatOptionsBinding;
import inspire.ariel.inspire.databinding.VhTreatBinding;


/**
 * {@link Treat Treat} View Holder possible statuses:
 * <pre>1. Purchaseable - show green purchase button</pre>
 * <pre>2. Not purchaseable or user is unauthorized  - hide purchase button</pre>
 * <pre>3. Sold out - show gray purchase button</pre>
 */

class TreatVH extends RecyclerView.ViewHolder {

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
        initUserStatusDataBasedViews(treat, position);
        initTreatStateBasedViews(treat, position);
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

        Log.d(TAG, treat.getText() + " is presented in the list as a treat");
    }

    @DebugLog
    private void initUserStatusDataBasedViews(Treat treat, int position) {
        treatBinding.optionsManagerBtn.setVisibility(DataManager.getInstance().getUserStatusData().getTreatOptionsVisibility());
        if(treatBinding.optionsManagerBtn.getVisibility() == View.VISIBLE){
            setTreatOptionsClickListener(treat, position);
        }
    }

    private void initTreatStateBasedViews(Treat treat, int position) {

        if (treat.getPurchasesLimit() == AppNumbers.NOT_PURCHASEABLE) { //If not purchaseable
            if (treat.getUserPurchases() > 0) { //User might have bought it earlier
                setTreatSoldOut();
            } else {
                treatBinding.purchaseBtn.setVisibility(View.GONE);
            }
            return;
        }

        if (treat.getUserPurchases() >= treat.getPurchasesLimit()) { //If user reached the limit
            setTreatSoldOut();
            return;
        }

        setTreatPurchaseable(treat, position);
    }

    private void setTreatSoldOut() {
        treatBinding.purchaseBtn.setBackgroundColor(Color.GRAY);
        treatBinding.purchaseBtn.setOnClickListener(view -> Log.d(TAG, "Treat already sold out"));
        treatBinding.purchaseBtn.setText(treatBinding.getRoot().getResources().getString(R.string.sold_out));
    }

    private void setTreatPurchaseable(Treat treat, int position){
        treatBinding.purchaseBtn.setBackgroundColor(Color.GREEN);
        treatBinding.purchaseBtn.setText(treatBinding.getRoot().getResources().getString(R.string.title_purchase));
        treatBinding.purchaseBtn.setOnClickListener(view -> onPurchaseClickListener.onClick(treat, position));
    }

    private void setTreatOptionsClickListener(Treat treat, int position){
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
    }
}
