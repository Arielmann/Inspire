package inspire.ariel.inspire.leader.quotecreatorfrag.view;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;
import inspire.ariel.inspire.leader.quotecreatorfrag.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotecreatorfrag.presenter.QuotesCreatorPresenterImpl;


public class QuotesCreatorActivity extends AppCompatActivity implements QuotesCreatorView {

    private ActivityQuoteCreatorBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_creator);
        QuotesCreatorPresenter presenter = new QuotesCreatorPresenterImpl(this, ((InspireApplication) getApplication()).getAppComponent());
        presenter.init(binding);
    }

    @Override
    public void changeBackground(Bitmap background) {
        binding.creatorLayout.setBackground(new BitmapDrawable(getResources(), background));
    }

    @Override
    public void changeQuoteTextSize(int value) {
        binding.quoteEditText.setTextSize(value);
    }
}
