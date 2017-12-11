package inspire.ariel.inspire.common.quoteslist.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.backendless.push.BackendlessPushService;

import java.util.Date;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.quoteslist.Quote;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;

public class PushNotificationService extends BackendlessPushService {

    private String TAG = PushNotificationService.class.getName();

    @Inject
    NotificationCompat.InboxStyle inboxStyle;

    @Inject
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        ((InspireApplication) getApplication()).getAppComponent().inject(this);
        super.onCreate();
    }

    //TODO: Support app messages as well (without quotes)
    @Override
    public boolean onMessage(Context context, Intent intent) {
        DataManager.getInstance().setMessagesSize(DataManager.getInstance().getMessagesSize() + 1);
        String notificationContentText = getResources().getString(R.string.new_quote_push_notification);
        Quote newQuote = parseQuote(intent);

        if (DataManager.getInstance().getMessagesSize() > 1) {
            notificationContentText = DataManager.getInstance().getMessagesSize() + AppStrings.SPACE_STRING + getResources().getString(R.string.push_notification_multiple_prefix) + AppStrings.SPACE_STRING + AppStrings.VAL_OWNER_NAME;
        }

        Notification newQuoteNotification = createNewQuoteNotification(notificationContentText, newQuote);

        if (notificationManager != null) {
            notificationManager.notify(AppStrings.NOTIFICATION_NEW_QUOTE_CHANNEL, AppNumbers.NOTIFICATIONS_NE_QUOTE_ID, newQuoteNotification);
        }

        // When returning 'true', default Backendless onMessage implementation will be executed.
        // The default implementation displays the notification in the Android Notification Center.
        // Returning false, cancels the execution of the default implementation.
        return false;
    }

    private Quote parseQuote(Intent intent) {
        String objectId = intent.getStringExtra(AppStrings.KEY_OBJECT_ID);
        String ownerId = intent.getStringExtra(AppStrings.KEY_OWNER_ID);
        String text = intent.getStringExtra(AppStrings.KEY_TEXT);
        String fontPath = intent.getStringExtra(AppStrings.KEY_FONT_PATH);
        String bgImageName = intent.getStringExtra(AppStrings.KEY_BG_IMAGE_NAME);
        Date creationDate = new Date();
        int textSize;
        int textColor;

        textSize = parseQuoteInteger(intent.getStringExtra(AppStrings.KEY_TEXT_SIZE), AppNumbers.DEFAULT_TEXT_SIZE);
        textColor = parseQuoteInteger(intent.getStringExtra(AppStrings.KEY_TEXT_COLOR), AppNumbers.DEFAULT_TEXT_COLOR);

        Quote quote = Quote.builder().objectId(objectId)
                .ownerId(ownerId)
                .created(creationDate)
                .text(text)
                .textSize(textSize)
                .fontPath(fontPath)
                .textColor(textColor)
                .bgImageName(bgImageName)
                .build();

        Log.i(TAG, "Quote created from push notification's intent: " + quote.toString());
        return quote;
    }

    private int parseQuoteInteger(String numString, int failureDefaultVal) {
        int result;

        try {
            result = Integer.parseInt(numString);
        } catch (NumberFormatException e) {
            result = failureDefaultVal;
        }
        return result;
    }

    private Notification createNewQuoteNotification(String contentText, Quote newQuote) {
        Intent intent = new Intent(this, QuotesListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(AppStrings.KEY_QUOTE, newQuote);
        PendingIntent quoteListPendingIntent = PendingIntent.getActivity(this, AppNumbers.DEFAULT_REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Builder(this, AppStrings.NOTIFICATION_NEW_QUOTE_CHANNEL)
                .setSmallIcon(R.drawable.thumb_up_white_18dp)
                .setContentTitle(AppStrings.VAL_OWNER_NAME)
                .setContentText(contentText)
                .setContentIntent(quoteListPendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .build();
    }

    @Override
    public void onError(Context context, String message) {
        Toast.makeText(context, getResources().getString(R.string.error_app_init), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRegistered(Context context, String registrationId) {
        //Toast.makeText( context, "device registered" + registrationId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnregistered(Context context, Boolean unregistered) {
        //Toast.makeText(context, "device unregistered", Toast.LENGTH_SHORT).show();
    }
}

