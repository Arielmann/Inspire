package inspire.ariel.inspire.leader.quotescreator.view;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenterImpl;


public class QuotesCreatorActivity extends AppCompatActivity implements QuotesCreatorView {

    private ActivityQuoteCreatorBinding binding;
    private QuotesCreatorPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_creator);
        QuoteOptionView quoteOptionView = (QuoteOptionView) getSupportFragmentManager().findFragmentById(R.id.quoteOptionsFrag);
        presenter = new QuotesCreatorPresenterImpl.Builder(this, ((InspireApplication) getApplication()).getAppComponent())
                .quoteOptionsFragView(quoteOptionView).build();
        KeyboardVisibilityEvent.setEventListener(this, keyboardVisibilityListener); //NOTE: Unregisters automatically on onDestroy()
    }

    KeyboardVisibilityEventListener keyboardVisibilityListener = isOpen -> {
        if (!isOpen) {
            binding.quoteEditText.clearFocus(); //Allow reappearance of background editor
        }
    };

    @Override
    public void setBackground(Bitmap background) {
        binding.creatorLayout.setBackground(new BitmapDrawable(getResources(), background));
    }

    @Override
    public void setQuoteTextSize(int size) {
        binding.quoteEditText.setTextSize(size);
    }

    @Override
    public void setQuoteFont(FontsManager.Font font) {
        FontsManager.getInstance().setFontOnTV(font, binding.quoteEditText);
    }

    @Override
    public void setQuoteTextColor(int color) {
        binding.quoteEditText.setHintTextColor(color);
        binding.quoteEditText.setTextColor(color);
    }

    @Override
    public void setBgPickerVisibility(int visibility) {
        binding.bgPicker.setVisibility(visibility);
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