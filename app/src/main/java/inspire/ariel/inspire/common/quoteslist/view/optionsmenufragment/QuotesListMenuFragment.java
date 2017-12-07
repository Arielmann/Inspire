package inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import inspire.ariel.inspire.databinding.FragmentQuoteListOptionsMenuBinding;

public class QuotesListMenuFragment extends Fragment implements QuoteListMenuView {

    /**
     * Takes care of all action regarding to menu buttons, including
     * presenting their related dialogs if any.
     */

    private static final String TAG = QuotesListMenuFragment.class.getName();
    private FragmentQuoteListOptionsMenuBinding binding;
    private QuotesListView quotesListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_list_options_menu, container, false);
        binding.loginImageBtn.setOnClickListener(onLoginClicked);
        return binding.getRoot();
    }

    private View.OnClickListener onLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new MaterialDialog.Builder(quotesListView.getContext())
                    .title(R.string.login)
                    .titleGravity(GravityEnum.CENTER)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .input(R.string.put_password_hint, R.string.empty_string, (dialog, input) -> {
                        quotesListView.showMainProgressDialog();
                        quotesListView.getPresenter().login(input, loginCallback);
                    }).show();
        }
    };

    private View.OnClickListener onLogoutClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            quotesListView.showMainProgressDialog();
            quotesListView.getPresenter().logout(logoutCallback);
        }
    };

    private GenericOperationCallback loginCallback = new GenericOperationCallback() {
        @Override
        public void onSuccess() {
            quotesListView.dismissMainProgressDialog();
            setLogoutAvailable();
            Log.i(TAG, "Login succeeded");
        }

        @Override
        public void onFailure(String reason) {
            quotesListView.onServerOperationFailed(getResources().getString(R.string.error_login));
            Log.e(TAG, "Login failed. reason: " + reason);
        }
    };

    GenericOperationCallback logoutCallback = new GenericOperationCallback() {
        @Override
        public void onSuccess() {
            quotesListView.dismissMainProgressDialog();
            setLoginAvailable();
            Log.i(TAG, "Logout succeeded");
        }

        @Override
        public void onFailure(String reason) {
            quotesListView.onServerOperationFailed(getResources().getString(R.string.error_logout));
            Log.e(TAG, "Logout failed. reason: " + reason);
        }
    };

    @Override
    public void setLogoutAvailable() {
        binding.loginImageBtn.animate().alpha(0).setDuration(AppTimeMillis.HALF_SECOND);
        binding.loginImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.blue_yellow_bg));
        binding.loginImageBtn.setOnClickListener(onLogoutClicked);
        binding.loginImageBtn.animate().alpha(1).setDuration(AppTimeMillis.HALF_SECOND);
    }

    @Override
    public void setLoginAvailable() {
        binding.loginImageBtn.animate().alpha(0).setDuration(AppTimeMillis.HALF_SECOND);
        binding.loginImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.female_icon));
        binding.loginImageBtn.setOnClickListener(onLoginClicked);
        binding.loginImageBtn.animate().alpha(1).setDuration(AppTimeMillis.HALF_SECOND);
    }

    @Override
    public void showLoginBtn(){
        binding.loginImageBtn.animate().alpha(0).setDuration(AppTimeMillis.HALF_SECOND);
        binding.loginImageBtn.setClickable(true);
        binding.loginImageBtn.setVisibility(View.VISIBLE);
        binding.loginImageBtn.animate().alpha(1).setDuration(AppTimeMillis.HALF_SECOND);
    }

    @Override
    public void init(QuotesListView quotesListView) {
        this.quotesListView = quotesListView;
    }
}
