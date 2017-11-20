package inspire.ariel.inspire.common.app;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Random;

import inspire.ariel.inspire.common.app.appinit.AppInitializer;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerAppComponent;
import inspire.ariel.inspire.common.di.ModelsModule;
import inspire.ariel.inspire.common.di.NetworkModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.constants.AppStrings;

public class InspireApplication extends Application {

    private static final String TAG = InspireApplication.class.getSimpleName();
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .networkModule(new NetworkModule()).modelsModule(new ModelsModule()).resourcesModule(new ResourcesModule(getResources(), getAssets())).build();
        AppInitializer initializer = new AppInitializer();
        initializer.InitApp(this);
       //registerDummyUser();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void registerDummyUser() {
        Random rand = new Random();
        BackendlessUser user = new BackendlessUser();
        user.setProperty(AppStrings.NAME, "Ariel" + rand.nextInt(1000-2) + 2);
        user.setProperty(AppStrings.DESCRIPTION, "I am a nice user");
        user.setEmail("arielmann" +  rand.nextInt(1000-2) + 2 + "@gmail.com");
        user.setPassword("iAmWatchingU");

        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            public void handleResponse(BackendlessUser registeredUser) {
                DataManager.getInstance().setUser(registeredUser);
            }

            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Leader registration error: " + fault.getCode());
            }
        });
    }
}
