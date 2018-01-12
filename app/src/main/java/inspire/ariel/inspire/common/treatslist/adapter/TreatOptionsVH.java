package inspire.ariel.inspire.common.treatslist.adapter;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.treatslist.events.OnTreatDeleteClickedEvent;
import inspire.ariel.inspire.common.treatslist.events.OnTreatsUpdateClickEvent;
import inspire.ariel.inspire.databinding.VhAdminTreatOptionsBinding;
import lombok.Getter;

public class TreatOptionsVH extends ViewHolder {

    private VhAdminTreatOptionsBinding binding;
    @Getter private int height;

    public TreatOptionsVH(VhAdminTreatOptionsBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        calculateLayoutHeight();
    }

    /**
     * Note: The total size is calculated based on each property within
     * the relative layout. All new properties height should
     * confirm to this fixed height(R.dimen.treat_option_height)
     */
    private void calculateLayoutHeight(){
        for (int i = 0; i < binding.treatOptionsManagerLayout.getChildCount(); i++) {
            height += binding.getRoot().getResources().getDimension(R.dimen.treat_option_height);
        }
    }

    public void init(DialogPlus dialog, Treat treat, int position) {
        binding.editBtn.setOnClickListener(view -> {
            EventBus.getDefault().post(new OnTreatsUpdateClickEvent(treat, position));
            dialog.dismiss();
        });

        binding.deleteBtn.setOnClickListener(view -> {
            EventBus.getDefault().post(new OnTreatDeleteClickedEvent(position));
            dialog.dismiss();
        });
    }
}
