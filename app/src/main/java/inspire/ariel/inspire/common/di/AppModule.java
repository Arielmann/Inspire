package inspire.ariel.inspire.common.di;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.utils.activityutils.viewpagerutils.ViewPagerAdapter;
import inspire.ariel.inspire.common.utils.errorutils.ErrorsManager;
import inspire.ariel.inspire.common.localdbmanager.RealmManager;

@Module

public class AppModule {

    private final InspireApplication application;
    private final NotificationCompat.InboxStyle inboxStyle;

    public AppModule(InspireApplication application) {
        this.application = application;
        this.inboxStyle = new NotificationCompat.InboxStyle();
    }

    @Provides
    @Singleton //Also being provided to other modules
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

    @Provides
    Handler provideHandler(){
        return new Handler();
    }

    @Provides
    @Singleton
    Calendar provideCalender(){
        return Calendar.getInstance();
    }

    @Provides
    @Singleton
    ErrorsManager provideErrorsManager(){
        return new ErrorsManager(application.getResources());
    }

    @Provides
    @Singleton
    RealmManager provideRealmManager(){
        return RealmManager.getInstance();
    }
}
