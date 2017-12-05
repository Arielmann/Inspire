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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.presenter.QuoteListPresenterImpl;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.leader.Leader;
import inspire.ariel.inspire.leader.quotescreator.model.QuoteCreatorModel;
import inspire.ariel.inspire.leader.quotescreator.view.QuotesCreatorView;
import lombok.Getter;

public class QuotesCreatorPresenterImpl implements QuotesCreatorPresenter {

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> quotesStorage;

    @Inject
    QuoteCreatorModel model;

    @Inject
    NetworkHelper networkHelper;

    private QuotesCreatorView creatorView;
    private String TAG = QuoteListPresenterImpl.class.getName();

    public QuotesCreatorPresenterImpl(AppComponent component, QuotesCreatorView view) {
        component.inject(this);
        this.creatorView = view;
    }

    /**
     * Lifecycle Methods
     */

    @Override
    public void onDestroy() {
        creatorView = null;
    }

    /**
     * Getters
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

    @Override
    public int getBgDrawableIntValue() {
        return model.getBgDrawableIntValue();
    }

    /**
     * Background Image Methods
     */

    @Getter private DiscreteScrollView.OnItemChangedListener onItemChangedListener = (viewHolder, adapterPosition) -> willSetBackgroundImage(adapterPosition);

    private void willSetBackgroundImage(int position) {
        model.setBgDrawableIntValue(customResourcesProvider.getBackgroundImages().get(position).getDrawableIntValue());
        model.setBgImageName(customResourcesProvider.getBackgroundImages().get(position).getName());
        if (customResourcesProvider.getBackgroundImages().get(position).getDrawable() != null) {
            creatorView.setBackground(customResourcesProvider.getBackgroundImages().get(position).getDrawable()); //The Only method call that is not made in order to prevent the background bug but actually meant to change the color upon user interaction
        }
    }

   @Getter private DiscreteScrollView.ScrollStateChangeListener onScrollStateChangedListener = new DiscreteScrollView.ScrollStateChangeListener() {
        @Override
        public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

        }

        @Override
        public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

        }

        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

        }
    };

    /**
     * Quote Validation
     */

    @Override
    public boolean validateQuote(String text) {
        if (text.replaceAll(AppStrings.REGEX_FIND_WHITESPACES, AppStrings.EMPTY_STRING).isEmpty()) {
            creatorView.dismissProgressDialogAndShowUploadErrorMessage(creatorView.getResources().getString(R.string.error_empty_quote));
            return false;
        }

        if (!NetworkHelper.getInstance().hasNetworkAccess(creatorView.getContext())) {
            creatorView.dismissProgressDialogAndShowUploadErrorMessage(creatorView.getResources().getString(R.string.error_no_connection));
            return false;
        }

        return true;
    }

    /**
     * Server Communication
     */

    //TODO: Protect from using the app id to post as this leader from a rouge device
    @Override
    public void postQuote(Quote quote) {
        if (networkHelper.hasNetworkAccess(creatorView.getContext())) {
            creatorView.showProgressDialog();
            quotesStorage.save(quote, new AsyncCallback<Quote>() {
                @Override
                public void handleResponse(Quote quote) {
                    sendPushNotification(DataManager.getInstance().getLeader(), quote);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    creatorView.dismissProgressDialogAndShowUploadErrorMessage(creatorView.getResources().getString(R.string.error_quote_upload));
                    Log.e(TAG, "Error saving quote to server: " + fault.getDetail());
                }
            });
        } else {
            creatorView.dismissProgressDialogAndShowUploadErrorMessage(creatorView.getResources().getString(R.string.error_quote_upload));
        }
    }

    /**
     * Menu Items Clicked Methods
     */

    @Override
    public void onQuoteFontClicked(String path) {
        model.setFontPath(path);
    }

    /**
     * NOTE for keys:
     * NOTIFICATION_HEADER_CONTENT_TEXT - For presenting in notification
     * KEY_TEXT - The actual quote for the app
     */
    //TODO: Don't register leader to it's own server channel or you get double newQuote on his machine! (until app restarts)
    private void sendPushNotification(Leader leader, Quote quote) {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_TICKER_TEXT, leader.getName());
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TITLE, leader.getName());
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TEXT, creatorView.getResources().getString(R.string.new_quote_push_notification));
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
                        creatorView.dismissProgressDialog();
                        quote.setCreated(new Date());
                        creatorView.goToQuoteListActivity(quote);
                        setLeaderQuoteRelation(leader, new ArrayList<Quote>() {{
                            add(quote);
                        }});
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Push notification sending error: " + fault.getDetail());
                        quote.setCreated(new Date());
                        creatorView.dismissProgressDialogAndShowUploadErrorMessage(creatorView.getResources().getString(R.string.push_notification_send_error));
                        creatorView.goToQuoteListActivity(quote);
                        setLeaderQuoteRelation(leader, new ArrayList<Quote>() {{
                            add(quote);
                        }});
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
                        Log.e(TAG, "Server reported an error: " + fault.getDetail());
                        //creatorView.dismissProgressDialogAndShowUploadErrorMessage(creatorView.getResources().getString(R.string.error_quote_leader_relation_creation));
                    }
                });
    }

    private void removeQuoteFromServer(Quote quote) {
        quotesStorage.remove(quote, new AsyncCallback<Long>() {
            @Override
            public void handleResponse(Long response) {
                Log.i(TAG, "quote removed from server. Quote details: " + quote.toString());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Quote removal failed for quote: " + quote.toString() + " consider manual removing in data base. Failure reason: " + fault.getDetail());
            }
        });
    }
}

