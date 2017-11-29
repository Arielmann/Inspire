package inspire.ariel.inspire.leader.quotescreator.presenter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> quotesStorage;

    @Inject
    QuoteCreatorModel model;

    private QuotesCreatorView view;
    private String TAG = QuoteListPresenterImpl.class.getName();

    private QuotesCreatorPresenterImpl(Builder builder) {
        builder.component.inject(this);
        this.view = builder.quoteCreatorActivityView;
        initBgPicker(view.getBgPicker());
        initQuoteOptionsView(builder.quoteOptionsView);
    }

    /**
     Init
     */
    private void initQuoteOptionsView(QuoteOptionsView quoteOptionsView) {
        quoteOptionsView.getFontsRV().setAdapter(new FontsAdapter(customResourcesProvider.getFonts(), font -> {
            view.setQuoteFont(font);
            model.setFontPath(font.getPath());
            view.setBackground(customResourcesProvider.getResources().getDrawable(model.getBgDrawableIntValue())); //Required to prevent background changes bug
        }));
        quoteOptionsView.getQuoteTextSizesRV().setAdapter(new FontSizesAdapter(customResourcesProvider.getFontsSizes(), textSize -> {
            view.setQuoteTextSize(textSize.getSize());
            view.setBackground(customResourcesProvider.getResources().getDrawable(model.getBgDrawableIntValue())); //Required to prevent background changes bug
        }));
        quoteOptionsView.getQuoteTextColorRV().setAdapter(new TextColorsAdapter(customResourcesProvider.getColors(), color -> view.setQuoteTextColor(color)));
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

    /**
     Getters
     */

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

    /**
     *Background Image changes
     */

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        willSetBackgroundImage(adapterPosition);
    }

    private void willSetBackgroundImage(int position) {
        model.setBgDrawableIntValue(customResourcesProvider.getBackgroundImages().get(position).getDrawableIntValue());
        model.setBgImageName(customResourcesProvider.getBackgroundImages().get(position).getName());
        if (customResourcesProvider.getBackgroundImages().get(position).getDrawable() != null) {
            view.setBackground(customResourcesProvider.getBackgroundImages().get(position).getDrawable()); //The Only method call that is not made in order to prevent the background bug but actually meant to change the color upon user interaction
        }
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

    /**
     * Quote Validation
     */

    @Override
    public boolean validateQuote(String text) {
        if (text.replaceAll(AppStrings.REGEX_FIND_WHITESPACES, AppStrings.EMPTY_STRING).isEmpty()) {
            view.dismissProgressDialogAndShowUploadErrorMessage(view.getResources().getString(R.string.error_empty_quote));
            return false;
        }

        if (!NetworkHelper.getInstance().hasNetworkAccess(view.getContext())) {
            view.dismissProgressDialogAndShowUploadErrorMessage(view.getResources().getString(R.string.error_no_connection));
            return false;
        }

        return true;
    }

    /**
     Server Communication
    */

    //TODO: Protect from using the app id to post_btn as this leader from a rouge device
    @Override
    public void postQuote(Quote quote) {
        view.showProgressDialog();
        quotesStorage.save(quote, new AsyncCallback<Quote>() {
            @Override
            public void handleResponse(Quote quote) {
                sendPushNotification(DataManager.getInstance().getLeader(), quote);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                view.dismissProgressDialogAndShowUploadErrorMessage(view.getResources().getString(R.string.error_quote_upload));
                Log.e(TAG, "Error saving quote to server: " + fault.toString());
            }
        });
    }

    private void sendPushNotification(Leader leader, Quote quote) {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_TICKER_TEXT, leader.getName());
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TITLE, leader.getName());
        //Content_Text - For presenting in notification | Quote_Text - The actual quote for the app
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TEXT, view.getResources().getString(R.string.new_quote_push_notification));
        publishOptions.putHeader(AppStrings.KEY_OBJECT_ID, quote.getObjectId());
        publishOptions.putHeader(AppStrings.KEY_LEADER_ID, quote.getLeaderId());
        publishOptions.putHeader(AppStrings.KEY_TEXT, quote.getText());
        publishOptions.putHeader(AppStrings.KEY_FONT_PATH, quote.getFontPath());
        publishOptions.putHeader(AppStrings.KEY_TEXT_SIZE, String.valueOf(quote.getTextSize()));
        publishOptions.putHeader(AppStrings.KEY_TEXT_COLOR, String.valueOf(quote.getTextColor()));
        publishOptions.putHeader(AppStrings.KEY_BG_IMAGE_NAME, quote.getBgImageName());

        Backendless.Messaging.publish(AppStrings.VAL_LEADER_NAME, AppStrings.SPACE_STRING,
                publishOptions, new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus response) {
                        Log.i(TAG, "Message sent");
                        view.dismissProgressDialog();
                        quote.setCreated(new Date());
                        view.goToQuoteListActivity(quote);
                        setLeaderQuoteRelation(leader, new ArrayList<Quote>(){{add(quote);}});
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Push notification sending error: " + fault.getMessage());
                        view.dismissProgressDialogAndShowUploadErrorMessage(view.getResources().getString(R.string.error_quote_upload));
                        removeQuoteFromServe(quote);
                    }
                });
    }


    private void setLeaderQuoteRelation(Leader leader, List<Quote> singleQuoteInsideList) {
        Backendless.Data.of(Leader.class).addRelation(leader, AppStrings.BACKENDLESS_TABLE_LEADER_COLUMN_QUOTES, singleQuoteInsideList,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        Log.i(TAG, "Relation has been set with quote: " + singleQuoteInsideList.get(0).getText() + " and leader: " + leader.getName());
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Server reported an error: " + fault.getMessage());
                        view.dismissProgressDialogAndShowUploadErrorMessage(view.getResources().getString(R.string.error_quote_leader_relation_creation));
                    }
                });
    }

    private void removeQuoteFromServe(Quote quote) {
        quotesStorage.remove(quote, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                Log.i(TAG, "quote removed from server. Quote details: " + quote.toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Quote removal failed for quote: " + quote.toString() + " consider manual removing in data base");
            }
        });
    }

    /**
     * Lifecycle Methods
     */
    @Override
    public void onDestroy() {
        view = null;
    }

    /**
     Builder
     */
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

