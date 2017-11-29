package inspire.ariel.inspire.common.di;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.constants.AppTimeMillis;
import inspire.ariel.inspire.common.quoteslist.view.QuotesListActivity;

@Module
public class AppModule {

    private InspireApplication application;
    private NotificationCompat.InboxStyle inboxStyle;

    public AppModule(InspireApplication application) {
        this.application = application;
        this.inboxStyle = new NotificationCompat.InboxStyle();
    }

    @Provides
    @Singleton
        //Also being provided to other modules
    InspireApplication providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    ActivityManager provideActivityManager() {
        return (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Provides
    @Singleton
    NotificationCompat.InboxStyle provideInspireInboxStyle() {
        return inboxStyle;
    }

    @Provides
    @Singleton
    NotificationManager provideNotificationManager(){
     return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
