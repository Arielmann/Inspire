package inspire.ariel.inspire.leader.quotescreator.presenter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.listutils.genericadapters.SingleBitmapListAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontSizesAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontsAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.TextColorsAdapter;
import inspire.ariel.inspire.leader.quotescreator.view.QuoteOptionView;
import inspire.ariel.inspire.leader.quotescreator.view.QuotesCreatorView;

public class QuotesCreatorPresenterImpl implements QuotesCreatorPresenter, DiscreteScrollView.OnItemChangedListener, DiscreteScrollView.ScrollStateChangeListener {

    @Inject
    ResourcesProvider model;

    @Inject
    Resources res;

    private QuotesCreatorView quotesCreatorView;

    private QuotesCreatorPresenterImpl(Builder builder) {
        builder.component.inject(this);
        this.quotesCreatorView = builder.quoteCreatorView;
        initBgPicker(quotesCreatorView.getBgPicker());
        initQuoteEditText(quotesCreatorView.getQuoteEditText());
        initQuoteOptionsFrag(builder.quoteOptionsView);
    }

    private void initQuoteOptionsFrag(QuoteOptionView quoteOptionView) {
        quoteOptionView.getFontsRV().setAdapter(new FontsAdapter(model.getFonts(), font -> quotesCreatorView.setQuoteFont(font)));
        quoteOptionView.getQuoteTextSizesRV().setAdapter(new FontSizesAdapter(model.getFontsSizes(), fontSize -> quotesCreatorView.setQuoteTextSize(fontSize.getSize())));
        quoteOptionView.getQuoteTextColorRV().setAdapter(new TextColorsAdapter(model.getColors(), color -> quotesCreatorView.setQuoteTextColor(color)));
    }

    @Override
    public void onDestroy() {
        quotesCreatorView = null;
    }

    private void initBgPicker(DiscreteScrollView bgPicker) {
        SingleBitmapListAdapter adapter = new SingleBitmapListAdapter(model.getBackgroundImages(), R.layout.vh_quote_bg_img);
        bgPicker.setSlideOnFling(false);
        bgPicker.setAdapter(adapter);
        bgPicker.addOnItemChangedListener(this);
        bgPicker.addScrollStateChangeListener(this);
        bgPicker.setItemTransitionTimeMillis(AppTimeMillis.QUARTER_SECOND);
        bgPicker.setOffscreenItems(0);
        bgPicker.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(AppInts.FLOAT_EIGHT_TENTHS)
                .build());
    }

    private void initQuoteEditText(EditText editText) {

        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                QuotesCreatorPresenterImpl.this.quotesCreatorView.setBgPickerVisibility(View.GONE);
            } else {
                QuotesCreatorPresenterImpl.this.quotesCreatorView.setBgPickerVisibility(View.VISIBLE);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > AppInts.QUOTE_LARGE_TEXT_MAX_LENGTH && editText.getTextSize() != res.getDimension(R.dimen.quote_small_text_size)) {
                    quotesCreatorView.setQuoteTextSize(30);
                    return;
                }

                if (editable.length() < AppInts.QUOTE_LARGE_TEXT_MAX_LENGTH && editText.getTextSize() == res.getDimension(R.dimen.quote_small_text_size)) {
                    quotesCreatorView.setQuoteTextSize(45);
                }
            }
        });
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        quotesCreatorView.setBackground(model.getBackgroundImages().get(adapterPosition));
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }

    public static class Builder {
        // Required parameters
        private final QuotesCreatorView quoteCreatorView;
        private final AppComponent component;

        // Optional parameters - initialized to default values
        private QuoteOptionView quoteOptionsView;

        public Builder(QuotesCreatorView view, AppComponent component) {
            this.quoteCreatorView = view;
            this.component = component;
        }

        public Builder quoteOptionsFragView(QuoteOptionView view) {
            quoteOptionsView = view;
            return this;
        }

        public QuotesCreatorPresenterImpl build() {
            return new QuotesCreatorPresenterImpl(this);
        }
    }
}

