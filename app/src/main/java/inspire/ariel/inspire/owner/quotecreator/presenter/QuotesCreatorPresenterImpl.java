package inspire.ariel.inspire.owner.quotecreator.presenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
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
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.owner.quotecreator.model.QuoteCreatorModel;
import inspire.ariel.inspire.owner.quotecreator.view.quotescreatoractivity.QuotesCreatorViewController;
import lombok.Getter;

public class QuotesCreatorPresenterImpl implements QuotesCreatorPresenter {

    @Inject
    ResourcesProvider customResourcesProvider;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_QUOTE)
    IDataStore<Quote> quotesStorage;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_USERS)
    IDataStore<BackendlessUser> usersStorage;

    @Inject
    QuoteCreatorModel model;

    @Inject
    NetworkHelper networkHelper;

    private String TAG = QuoteListPresenterImpl.class.getName();
    private QuotesCreatorViewController quoteCreatorViewController;

    @Inject public QuotesCreatorPresenterImpl(AppComponent component, QuotesCreatorViewController quoteCreatorViewController) {
        component.inject(this);
        this.quoteCreatorViewController = quoteCreatorViewController;
    }

    /**
     * Lifecycle Methods
     */

    @Override
    public void onDestroy() {
        quoteCreatorViewController = null;
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

    /**
     * Background Image Methods
     */

    @Getter private DiscreteScrollView.OnItemChangedListener onItemChangedListener = (viewHolder, adapterPosition) -> willSetBackgroundImage(adapterPosition);

    private void willSetBackgroundImage(int position) {
        model.setBgDrawableIntValue(customResourcesProvider.getBackgroundImages().get(position).getDrawableIntValue());
        model.setBgImageName(customResourcesProvider.getBackgroundImages().get(position).getName());
        if (customResourcesProvider.getBackgroundImages().get(position).getDrawable() != null) {
            quoteCreatorViewController.setBackground(customResourcesProvider.getBackgroundImages().get(position).getDrawable()); //The Only method call that is not made in order to prevent the background bug but actually meant to change the color upon user interaction
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
            quoteCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_empty_quote));
            return false;
        }

        if (!NetworkHelper.getInstance().hasNetworkAccess(quoteCreatorViewController.getContext())) {
            quoteCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_no_connection));
            return false;
        }

        return true;
    }

    /**
     * Server Communication
     */

    //TODO: Protect from using the app id to post as this user from a rouge device
    @Override
    public void postQuote(Quote quote) {
        if (networkHelper.hasNetworkAccess(quoteCreatorViewController.getContext())) {
            quoteCreatorViewController.showProgressDialog();
            quotesStorage.save(quote, new AsyncCallback<Quote>() {
                @Override
                public void handleResponse(Quote quote) {
                    sendPushNotification(DataManager.getInstance().getUser(), quote);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    quoteCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_quote_upload));
                    if(fault.getCode().equals(AppStrings.BACKENDLESS_ERROR_CODE_NO_PERMISSION_ERROR)){
                        quoteCreatorViewController.showErrorDialogAndGoBackToQuoteListActivity();
                        return;
                    }
                    Log.e(TAG, "Error saving quote to server: " + fault.getDetail());
                }
            });
        } else {
            quoteCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.error_quote_upload));
        }
    }


    /**
     * NOTE for keys:
     * NOTIFICATION_HEADER_CONTENT_TEXT - For presenting in notification
     * KEY_TEXT - The actual quote for the app
     */
    //TODO: Don't register user to it's own server channel or you get double newQuote on his machine! (until app restarts)
    private void sendPushNotification(BackendlessUser user, Quote quote) {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_TICKER_TEXT, String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TITLE, String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
        publishOptions.putHeader(AppStrings.NOTIFICATION_HEADER_CONTENT_TEXT, customResourcesProvider.getResources().getString(R.string.new_quote_push_notification));
        publishOptions.putHeader(AppStrings.KEY_OBJECT_ID, quote.getObjectId());
        publishOptions.putHeader(AppStrings.KEY_OWNER_ID, quote.getOwnerId());
        publishOptions.putHeader(AppStrings.KEY_TEXT, quote.getText());
        publishOptions.putHeader(AppStrings.KEY_FONT_PATH, quote.getFontPath());
        publishOptions.putHeader(AppStrings.KEY_TEXT_SIZE, String.valueOf(quote.getTextSize()));
        publishOptions.putHeader(AppStrings.KEY_TEXT_COLOR, String.valueOf(quote.getTextColor()));
        publishOptions.putHeader(AppStrings.KEY_BG_IMAGE_NAME, quote.getBgImageName());

        Backendless.Messaging.publish(AppStrings.VAL_OWNER_NAME, AppStrings.SPACE_STRING, publishOptions, new AsyncCallback<MessageStatus>() {
                    @Override
                    public void handleResponse(MessageStatus response) {
                        Log.i(TAG, "Message sent");
                        quoteCreatorViewController.dismissProgressDialog();
                        quote.setCreated(new Date());
                        Intent intent = new Intent(quoteCreatorViewController.getContext(), QuotesListActivity.class);
                        intent.putExtra(AppStrings.KEY_QUOTE, quote);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        quoteCreatorViewController.goToOtherActivity(intent);
                        setUserQuoteRelation(user, new ArrayList<Quote>() {{
                            add(quote);
                        }});
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Push notification sending error: " + fault.getDetail());
                        quote.setCreated(new Date());
                        quoteCreatorViewController.dismissProgressDialogAndShowErrorMessage(customResourcesProvider.getResources().getString(R.string.push_notification_send_error));
                        Intent intent = new Intent().putExtra(AppStrings.KEY_QUOTE, quote);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        quoteCreatorViewController.goToOtherActivity(intent);
                        setUserQuoteRelation(user, new ArrayList<Quote>() {{
                            add(quote);
                        }});
                    }
                });
    }

    private void setUserQuoteRelation(BackendlessUser user, List<Quote> singleQuoteInsideList) {
        usersStorage.addRelation(user, AppStrings.BACKENDLESS_TABLE_USER_COLUMN_QUOTES, singleQuoteInsideList,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(Integer response) {
                        Log.i(TAG, "Relation has been set with quote: " + singleQuoteInsideList.get(0).getText() + " and user: " + String.valueOf(user.getProperty(AppStrings.KEY_NAME)));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Server reported an error: " + fault.getDetail());
                        //quoteCreatorViewController.dismissProgressDialogAndShowErrorMessage(quoteCreatorViewController.getResources().getString(R.string.error_quote_leader_relation_creation));
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

    /**
     * Menu Items Clicked Methods
     */

    @Override
    public void onQuoteFontClicked(String path) {
        model.setFontPath(path);
    }

}

