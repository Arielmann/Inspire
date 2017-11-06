package inspire.ariel.inspire.leader.quotecreatorfrag.presenter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.databinding.ActivityQuoteCreatorBinding;
import inspire.ariel.inspire.leader.quotecreatorfrag.adapter.BgItemsAdapter;
import inspire.ariel.inspire.leader.quotecreatorfrag.view.QuotesCreatorView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;

public class QuotesCreatorPresenterImpl implements QuotesCreatorPresenter, DiscreteScrollView.OnItemChangedListener, DiscreteScrollView.ScrollStateChangeListener {

    @Inject
    ResourcesProvider model;

    @Inject
    Resources res;

    private BgItemsAdapter adapter;
    private QuotesCreatorView view;

    public QuotesCreatorPresenterImpl(QuotesCreatorView view, AppComponent component) {
        component.inject(this);
        this.view = view;
        adapter = new BgItemsAdapter(model.getBackgroundImages());
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void init(ActivityQuoteCreatorBinding binding) {
       initBgPicker(binding.bgPicker);
       initQuoteEditText(binding.quoteEditText);

    }

    private void initBgPicker(DiscreteScrollView bgPicker) {
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

    private void initQuoteEditText(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > AppInts.QUOTE_LARGE_TEXT_MAX_LENGTH && editText.getTextSize() != res.getDimension(R.dimen.quote_small_text_size)){
                    view.changeQuoteTextSize(30);
                    return;
                }

                if(editable.length() < AppInts.QUOTE_LARGE_TEXT_MAX_LENGTH && editText.getTextSize() == res.getDimension(R.dimen.quote_small_text_size)){
                    view.changeQuoteTextSize(45);
                }
            }
        });
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        view.changeBackground(model.getBackgroundImages().get(adapterPosition));
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
}
