package inspire.ariel.inspire.leader.quotescreator.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenter;
import inspire.ariel.inspire.leader.quotescreator.presenter.QuotesCreatorPresenterImpl;

public class QuotesCreatorActivity extends AppCompatActivity implements QuotesCreatorView {

    private String TAG = QuotesCreatorActivity.class.getName();
    private ActivityQuoteCreatorBinding binding;
    private QuotesCreatorPresenter presenter;
    private ImageButton postImgButton;  //TODO: Check why it doesn't work on data binding!

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_creator);
        postImgButton = findViewById(R.id.postImgButton);
        postImgButton.setOnClickListener(onPostQuoteClicked);
        setQuoteFont(FontsManager.Font.ALEF_BOLD);
        setQuoteTextColor(Color.BLACK);
        QuoteOptionView quoteOptionView = (QuoteOptionView) getSupportFragmentManager().findFragmentById(R.id.quoteOptionsFrag);
        presenter = new QuotesCreatorPresenterImpl.Builder(this, ((InspireApplication) getApplication()).getAppComponent())
                .quoteOptionsFragView(quoteOptionView).build();
        initKeyboardChangeBehaviours(binding.quoteEditText);
    }

    private void initKeyboardChangeBehaviours(EditText quoteEditText) {

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if(!isOpen) {
                binding.quoteEditText.clearFocus(); //Allow reappearance of background editor
            }
        });

        quoteEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                postImgButton.setVisibility(View.GONE);
                binding.bgPicker.setVisibility(View.GONE);
            } else {
                postImgButton.setVisibility(View.VISIBLE);
                binding.bgPicker.setVisibility(View.VISIBLE);
            }
        });
    }

    View.OnClickListener onPostQuoteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String text = binding.quoteEditText.getText().toString();
            final int textColor = binding.quoteEditText.getCurrentTextColor();
            final int textSize = Math.round(binding.quoteEditText.getTextSize());

            Uri uri = Uri.parse(AppStrings.DRAWABLE_PATH_PREFIX + presenter.getBgImgName());

            if(!text.isEmpty()) {
                Quote quote = Quote.builder().text(text)
                        .fontPath(presenter.getFontPath())
                        .textColor(textColor)
                        .textSize(textSize)
                        .bgImageName(presenter.getBgImgName())
                        .build();
                presenter.postQuote(quote);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setBackground(Drawable background) {
        binding.creatorLayout.setBackground(background);
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
    public DiscreteScrollView getBgPicker() {
        return binding.bgPicker;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
