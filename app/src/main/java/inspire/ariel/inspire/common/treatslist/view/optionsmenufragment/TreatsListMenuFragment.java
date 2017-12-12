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

public class TreatsListMenuFragment extends Fragment implements TreatListMenuView {

    /**
     * Takes care of all action regarding to menu buttons, including
     * presenting their related dialogs if any.
     */

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
        binding.loginLogoutImageBtn.setOnClickListener(onLoginClicked);
        return binding.getRoot();
    }

    @Getter private View.OnClickListener onLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new MaterialDialog.Builder(treatsListView.getContext())
                    .title(R.string.login)
                    .titleGravity(GravityEnum.CENTER)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .input(R.string.put_password_hint, R.string.empty_string, (dialog, input) -> {
                        treatsListView.showProgressDialog(treatsListView.getLoginLogoutProgressDialog());
                        treatsListView.getPresenter().login(input);
                    }).show();
        }
    };

    @Getter private View.OnClickListener onLogoutClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            treatsListView.showProgressDialog(treatsListView.getLoginLogoutProgressDialog());
            treatsListView.getPresenter().logout();
        }
    };

    @Override
    public void resetLoginLogoutBtn(Drawable imgDrawable, View.OnClickListener newListener) {
        binding.loginLogoutImageBtn.animate().alpha(0).setDuration(AppTimeMillis.HALF_SECOND);
        binding.loginLogoutImageBtn.setClickable(true);
        binding.loginLogoutImageBtn.setVisibility(View.VISIBLE);
        binding.loginLogoutImageBtn.setImageDrawable(imgDrawable);
        binding.loginLogoutImageBtn.setOnClickListener(newListener);
        binding.loginLogoutImageBtn.animate().alpha(1).setDuration(AppTimeMillis.HALF_SECOND);
    }

    @Override
    public void init(TreatsListView treatsListView) {
        this.treatsListView = treatsListView;
    }
}
