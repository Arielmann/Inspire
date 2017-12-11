package inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment;

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
import inspire.ariel.inspire.common.quoteslist.view.QuotesListView;
import inspire.ariel.inspire.databinding.FragmentQuoteListMenuBinding;
import lombok.Getter;

public class QuotesListMenuFragment extends Fragment implements QuoteListMenuView {

    /**
     * Takes care of all action regarding to menu buttons, including
     * presenting their related dialogs if any.
     */

    private static final String TAG = QuotesListMenuFragment.class.getName();
    private FragmentQuoteListMenuBinding binding;
    private QuotesListView quotesListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_list_menu, container, false);
        binding.loginLogoutImageBtn.setOnClickListener(onLoginClicked);
        return binding.getRoot();
    }

    @Getter private View.OnClickListener onLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new MaterialDialog.Builder(quotesListView.getContext())
                    .title(R.string.login)
                    .titleGravity(GravityEnum.CENTER)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .input(R.string.put_password_hint, R.string.empty_string, (dialog, input) -> {
                        quotesListView.showProgressDialog(quotesListView.getLoginLogoutProgressDialog());
                        quotesListView.getPresenter().login(input);
                    }).show();
        }
    };

    @Getter private View.OnClickListener onLogoutClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            quotesListView.showProgressDialog(quotesListView.getLoginLogoutProgressDialog());
            quotesListView.getPresenter().logout();
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
    public void init(QuotesListView quotesListView) {
        this.quotesListView = quotesListView;
    }
}
