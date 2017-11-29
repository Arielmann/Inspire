package inspire.ariel.inspire.common.app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerAppComponent;
import inspire.ariel.inspire.common.di.ModelsModule;
import inspire.ariel.inspire.common.di.NetworkModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.quoteslist.view.ContinuousOperationCallback;
import inspire.ariel.inspire.common.resources.ResourcesInitializer;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.leader.Leader;

public class InspireApplication extends Application {

    @Inject
    ResourcesInitializer resourcesInitializer;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> leadersStorage;

    private static final String TAG = InspireApplication.class.getSimpleName();
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(this))
                .networkModule(new NetworkModule()).modelsModule(new ModelsModule()).resourcesModule(new ResourcesModule(getResources(), getAssets())).build();
        //registerDummyUser();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void registerDummyUser() {
        Random rand = new Random();
        BackendlessUser user = new BackendlessUser();
        user.setProperty(AppStrings.KEY_NAME, "Ariel" + rand.nextInt(1000 - 2) + 2);
        user.setProperty(AppStrings.KEY_DESCRIPTION, "I am a nice user");
        user.setEmail("arielmann" + rand.nextInt(1000 - 2) + 2 + "@gmail.com");
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

    public void initApp(ContinuousOperationCallback operationCallback) {
        if (NetworkHelper.getInstance().hasNetworkAccess(this)) {
            this.getAppComponent().inject(this);
            Backendless.initApp(this, AppStrings.BACKENDLESS_VAL_APPLICATION_ID, AppStrings.BACKENDLESS_VAL_API_KEY);
            resourcesInitializer.init(this);
            FontsManager.getInstance().init(this);
            registerDeviceToBackendless(AppStrings.BACKENDLESS_DEFAULT_CHANNEL, () -> Toast.makeText(InspireApplication.this, getResources().getString(R.string.error_general_channel_registration), Toast.LENGTH_LONG).show());
            registerDeviceToBackendless(AppStrings.VAL_LEADER_NAME, operationCallback);
        } else {
            operationCallback.onFailure();
        }
    }

    //TODO: Register without expire dates
    private void registerDeviceToBackendless(String channel, ContinuousOperationCallback operationCallback) {
        Backendless.Messaging.registerDevice(AppStrings.VAL_SENDER_ID, channel, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                operationCallback.onSuccess();
                Log.d(TAG, "Successfully registered device to backendless");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                operationCallback.onFailure();
                Log.d(TAG, "Error registering device: " + fault.getMessage());
            }
        });
    }
}
