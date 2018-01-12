package inspire.ariel.inspire.common.treatslist.view.optionsmenufragment;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.treatslist.view.TreatsListView;
import inspire.ariel.inspire.databinding.FragmentTreatListMenuBinding;
import lombok.Getter;

/**
 * Takes care of all action regarding to menu buttons, including
 * presenting their related dialogs if any.
 * <pre>Must be attached to a {@link TreatsListView TreatListView} delegate</pre>
 */
public class TreatsListMenuFragment extends Fragment {


    private static final String TAG = TreatsListMenuFragment.class.getName();
    private FragmentTreatListMenuBinding binding;
    private TreatsListView treatsListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_treat_list_menu, container, false);
        binding.loginLogoutImageBtn.setOnClickListener(onLogoutClicked);
        treatsListView = (TreatsListView) getActivity();
        return binding.getRoot();
    }

    @Getter
    private View.OnClickListener onLogoutClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new MaterialDialog.Builder(treatsListView.getContext())
                    .title(R.string.really_logout_title)
                    .titleGravity(GravityEnum.CENTER)
                    .contentGravity(GravityEnum.CENTER)
                    .positiveText(R.string.ok)
                    .onPositive((dialog, which) -> {
                        treatsListView.showProgressDialog(treatsListView.getLoginLogoutProgressDialog());
                        treatsListView.getPresenter().logout();
                    })
                    .negativeText(R.string.cancel)
                    .onNegative((dialog, which) -> dialog.cancel())
                    .cancelable(true)
                    .show();
        }
    };
}