package inspire.ariel.inspire.leader.quotescreator.presenter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.common.utils.listutils.genericadapters.SingleBitmapListAdapter;
import inspire.ariel.inspire.leader.Leader;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontSizesAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.FontsAdapter;
import inspire.ariel.inspire.leader.quotescreator.adapters.TextColorsAdapter;
import inspire.ariel.inspire.leader.quotescreator.model.QuoteCreatorModel;
import inspire.ariel.inspire.leader.quotescreator.view.QuoteOptionsView;
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
        this.quotesCreatorView = builder.quoteCreatorActivityView;
        initBgPicker(quotesCreatorView.getBgPicker());
        initQuoteOptionsView(builder.quoteOptionsView);
    }

    private void initQuoteOptionsView(QuoteOptionsView quoteOptionsView) {
        quoteOptionsView.getFontsRV().setAdapter(new FontsAdapter(customResourcesProvider.getFonts(), font -> {
            quotesCreatorView.setQuoteFont(font);
            model.setFontPath(font.getPath());
            quotesCreatorView.setBackground(customResourcesProvider.getResources().getDrawable(model.getBgDrawableIntValue())); //Required to prevent background changes bug
        }));
        quoteOptionsView.getQuoteTextSizesRV().setAdapter(new FontSizesAdapter(customResourcesProvider.getFontsSizes(), textSize -> {
            quotesCreatorView.setQuoteTextSize(textSize.getSize());
            quotesCreatorView.setBackground(customResourcesProvider.getResources().getDrawable(model.getBgDrawableIntValue())); //Required to prevent background changes bug
        }));
        quoteOptionsView.getQuoteTextColorRV().setAdapter(new TextColorsAdapter(customResourcesProvider.getColors(), color -> quotesCreatorView.setQuoteTextColor(color)));
    }

    @Override
    public void onDestroy() {
        quotesCreatorView = null;
    }

    //TODO: Protect from using the app id to post_btn as this leader from a rouge device
    @Override
    public void postQuote(Quote quote) {
        quotesCreatorView.showProgressDialog();
        quotesStorage.save(quote, new AsyncCallback<Quote>() {
            @Override
            public void handleResponse(Quote response) {
                List<Quote> quoteInList = new ArrayList<>();
                quoteInList.add(response);
                setLeaderQuoteRelation(DataManager.getInstance().getLeader(), quoteInList);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                quotesCreatorView.showUploadErrorMessage(quotesCreatorView.getResources().getString(R.string.error_no_connection));
                Log.e(TAG, "Error saving quote to server: " + fault.toString());
            }
        });
    }

    @Override
    public boolean validateQuote(String text) {
        if (text.replaceAll(AppStrings.FIND_WHITESPACES_REGEX, AppStrings.EMPTY_STRING).isEmpty()) {
            quotesCreatorView.showUploadErrorMessage(quotesCreatorView.getResources().getString(R.string.error_empty_quote));
            return false;
        }

        if(!NetworkHelper.getInstance().hasNetworkAccess(quotesCreatorView.getContext())){
            quotesCreatorView.showUploadErrorMessage(quotesCreatorView.getResources().getString(R.string.error_no_connection));
            return false;
        }

        return true;
    }

    private void setLeaderQuoteRelation(Leader leader, List<Quote> singleQuoteInsideList) {
        Backendless.Data.of(Leader.class).addRelation(leader, AppStrings.BACKENDLESS_TABLE_LEADER_COLUMN_QUOTES, singleQuoteInsideList,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        Log.i(TAG, "Relation has been set with quote: " + singleQuoteInsideList.get(0).getText() + " and leader: " + leader.getName());
                        quotesCreatorView.goToQuoteListActivity();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Server reported an error: " + fault.getMessage());
                        quotesCreatorView.showUploadErrorMessage(quotesCreatorView.getResources().getString(R.string.error_no_connection));
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

    @Override
    public Drawable getChosenBgImage() {
        return customResourcesProvider.getResources().getDrawable(model.getBgDrawableIntValue());
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
       willSetBackgroundImage(adapterPosition);
    }

    private void willSetBackgroundImage(int position){
        model.setBgDrawableIntValue(customResourcesProvider.getBackgroundImages().get(position).getDrawableIntValue());
        model.setBgImageName(customResourcesProvider.getBackgroundImages().get(position).getName());
        if (customResourcesProvider.getBackgroundImages().get(position).getDrawable() != null) {
            quotesCreatorView.setBackground(customResourcesProvider.getBackgroundImages().get(position).getDrawable()); //The Only method call that is not made in order to prevent the background bug but actually meant to change the color upon user interaction
        }
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {}

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {}

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {}

    public static class Builder {
        // Required parameters
        private final QuotesCreatorView quoteCreatorActivityView;
        private final AppComponent component;

        // Optional parameters - initialized to default values
        private QuoteOptionsView quoteOptionsView;

        public Builder(QuotesCreatorView view, AppComponent component) {
            this.quoteCreatorActivityView = view;
            this.component = component;
        }

        public Builder quoteOptionsView(QuoteOptionsView view) {
            quoteOptionsView = view;
            return this;
        }

        public QuotesCreatorPresenterImpl build() {
            return new QuotesCreatorPresenterImpl(this);
        }
    }
}

