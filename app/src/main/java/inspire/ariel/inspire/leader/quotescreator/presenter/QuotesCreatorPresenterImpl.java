package inspire.ariel.inspire.leader.quotescreator.presenter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.activityutils.ActivityStarter;
import inspire.ariel.inspire.common.utils.listutils.genericadapters.SingleBitmapListAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontSizesAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontsAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.TextColorsAdapter;
import inspire.ariel.inspire.leader.quotescreator.model.QuoteCreatorModel;
import inspire.ariel.inspire.leader.quotescreator.view.QuoteOptionView;
import inspire.ariel.inspire.leader.quotescreator.view.QuotesCreatorView;

public class QuotesCreatorPresenterImpl implements QuotesCreatorPresenter, DiscreteScrollView.OnItemChangedListener, DiscreteScrollView.ScrollStateChangeListener {

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    Resources res;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> quotesStorage;

    @Inject
    QuoteCreatorModel model;

    private QuotesCreatorView quotesCreatorView;
    private String TAG = QuoteListPresenterImpl.class.getName();

    private QuotesCreatorPresenterImpl(Builder builder) {
        builder.component.inject(this);
        this.quotesCreatorView = builder.quoteCreatorView;
        initBgPicker(quotesCreatorView.getBgPicker());
        initQuoteOptionsFrag(builder.quoteOptionsView);
    }

    private void initQuoteOptionsFrag(QuoteOptionView quoteOptionView) {
        quoteOptionView.getFontsRV().setAdapter(new FontsAdapter(customResourcesProvider.getFonts(), font -> {
            quotesCreatorView.setQuoteFont(font);
            model.setFontPath(font.getPath());
        }));
        quoteOptionView.getQuoteTextSizesRV().setAdapter(new FontSizesAdapter(customResourcesProvider.getFontsSizes(), fontSize -> quotesCreatorView.setQuoteTextSize(fontSize.getSize())));
        quoteOptionView.getQuoteTextColorRV().setAdapter(new TextColorsAdapter(customResourcesProvider.getColors(), color -> quotesCreatorView.setQuoteTextColor(color)));
    }

    @Override
    public void onDestroy() {
        quotesCreatorView = null;
    }

    @Override
    public void postQuote(Quote quote) {
        quotesStorage.save(quote, new AsyncCallback<Quote>() {
            @Override
            public void handleResponse(Quote response) {
                ActivityStarter.startActivity(quotesCreatorView.getContext(), QuotesListActivity.class);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(quotesCreatorView.getContext(), "There was an error posting the quote, please try again", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error saving quote to server: " + fault.toString());
            }
        });
    }

    @Override
    public String getBgImgName() {
        return model.getBgImageName();
    }

    @Override
    public String getFontPath() {
        return model.getFontPath();
    }

    private void initBgPicker(DiscreteScrollView bgPicker) {
        SingleBitmapListAdapter adapter = new SingleBitmapListAdapter(customResourcesProvider.getBackgroundImages(), R.layout.vh_quote_bg_img);
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

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        model.setBgImageName(customResourcesProvider.getBackgroundImages().get(adapterPosition).getName());
        quotesCreatorView.setBackground(customResourcesProvider.getBackgroundImages().get(adapterPosition).getDrawable());
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

