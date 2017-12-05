package inspire.ariel.inspire.common.quoteslist.view.optionsmenufragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenter;
import inspire.ariel.inspire.databinding.FragmentQuoteListOptionsMenuBinding;
import lombok.Setter;

public class QuoteListMenuFragment extends Fragment implements QuoteListMenuView {

    private FragmentQuoteListOptionsMenuBinding binding;
    @Setter private QuoteListPresenter quoteListPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_list_options_menu, container, false);
        return binding.getRoot();
    }

    private View.OnClickListener onLoginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}
