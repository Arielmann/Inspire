package inspire.ariel.inspire.leader.quotecreatorfrag.view;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.utils.actionbarutils.QuoteOptionItemsNavigationAdapter;
import inspire.ariel.inspire.common.utils.actionbarutils.model.SpinnerNavItem;
import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;
import inspire.ariel.inspire.leader.quotecreatorfrag.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotecreatorfrag.presenter.QuotesCreatorPresenterImpl;


public class QuotesCreatorActivity extends AppCompatActivity implements QuotesCreatorView {

    private ActivityQuoteCreatorBinding binding;
    private QuotesCreatorPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_creator);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        presenter = new QuotesCreatorPresenterImpl(this, ((InspireApplication) getApplication()).getAppComponent());
        KeyboardVisibilityEvent.setEventListener(this, keyboardVisibilityListener); //NOTE: Unregisters automatically on onDestroy()

        ActionBar actionBar = getSupportActionBar();

        // Spinner title navigation data
        ArrayList<SpinnerNavItem> navSpinner  = new ArrayList<>();
        navSpinner.add(new SpinnerNavItem("Local", R.drawable.female_icon));
        navSpinner.add(new SpinnerNavItem("My Places", R.drawable.bg3));
        navSpinner.add(new SpinnerNavItem("Checkins", R.drawable.bg4));

        // title drop down adapter
        QuoteOptionItemsNavigationAdapter adapter = new QuoteOptionItemsNavigationAdapter(getApplicationContext(),
                navSpinner);

    }

    KeyboardVisibilityEventListener keyboardVisibilityListener = isOpen -> {
        binding.bgPicker.setVisibility(View.VISIBLE);
        if (isOpen) {
            binding.bgPicker.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quotes_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chooseQuoteFontImgBtn:

                return true;

            case R.id.chooseQuoteTextColorImgBtn:
                return true;

            case R.id.chooseQuoteTextSizeImgBtn:

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void changeBackground(Bitmap background) {
        binding.creatorLayout.setBackground(new BitmapDrawable(getResources(), background));
    }


    @Override
    public void changeQuoteTextSize(int value) {
        binding.quoteEditText.setTextSize(value);
    }

    @Override
    public DiscreteScrollView getBgPicker() {
        return binding.bgPicker;
    }

    @Override
    public EditText getQuoteEditText() {
        return binding.quoteEditText;
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
