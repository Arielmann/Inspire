package inspire.ariel.inspire.common.di;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import inspire.ariel.inspire.common.app.InspireApplication;

@Module
public class AppModule {

    private InspireApplication application;

    public AppModule(InspireApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton //Also being provided to other modules
    InspireApplication providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    ActivityManager provideActivityManager(){
        return (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
    }

}
