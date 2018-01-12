package inspire.ariel.inspire.common.app;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import hugo.weaving.DebugLog;
import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.datamanager.DataManager;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerAppComponent;
import inspire.ariel.inspire.common.di.ModelsModule;
import inspire.ariel.inspire.common.di.NetworkModule;
import inspire.ariel.inspire.common.di.RecyclerViewsModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.resources.ResourcesInitializer;
import inspire.ariel.inspire.common.utils.asyncutils.CodependentTasksManager;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import io.realm.Realm;

@DebugLog
public class InspireApplication extends Application {

    @Inject
    ResourcesInitializer resourcesInitializer;

    @Inject
    Calendar calendar;

    private static final String TAG = InspireApplication.class.getSimpleName();
    private AppComponent appComponent;
    private CodependentTasksManager firstInitTasksManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }

    private void initAppComponent(){
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .modelsModule(new ModelsModule(this))
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .recyclerViewsModule(new RecyclerViewsModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void initAppForFirstTimeIfNeeded(GenericOperationCallback operationCallback) {
        if (!Hawk.contains(AppStrings.KEY_IS_FIRST_LAUNCH) || (boolean)Hawk.get(AppStrings.KEY_IS_FIRST_LAUNCH)) {
            Hawk.put(AppStrings.KEY_IS_FIRST_LAUNCH, true);
            firstInitTasksManager = new CodependentTasksManager(operationCallback, AppNumbers.MUST_COMPLETED_TASKS_ON_FIRST_LAUNCH);
            initFirstLaunch();
        } else {
            Hawk.put(AppStrings.KEY_IS_FIRST_LAUNCH, false);
            operationCallback.onSuccess();
        }
    }

    private void initApp() {
        initAppComponent();
        Realm.init(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        this.getAppComponent().inject(this);
        Hawk.init(this).build();
        Backendless.initApp(this, AppStrings.BACKENDLESS_VAL_APPLICATION_ID, AppStrings.BACKENDLESS_VAL_API_KEY);
        resourcesInitializer.init(this);
        FontsManager.getInstance().init(this);
    }

    /**
     * NOTE:
     * Don't forget to increase firstInitTasksManager's "mustSucceedOperations"
     * if more required operations are added to this method
     */
    private void initFirstLaunch() {
        Hawk.put(AppStrings.KEY_LOGGED_IN_USER, new BackendlessUser()); //Prevents crushes upon access
        Hawk.put(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER, true); //No one has connected yet
        List<String> channels = new ArrayList<String>() {{
            add(AppStrings.BACKENDLESS_DEFAULT_CHANNEL);
            add(AppStrings.VAL_OWNER_NAME);
        }};
        registerDeviceToBackendless(channels);
    }

    //TODO: Register one time only
    private void registerDeviceToBackendless(List<String> channels) {
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, AppNumbers.BACKENDLESS_REGISTRATION_CHANNEL_DAYS_LIMIT);
        Backendless.Messaging.registerDevice(AppStrings.VAL_SENDER_ID, channels, calendar.getTime(), new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                firstInitTasksManager.onSingleOperationSuccessful();
                Log.d(TAG, "Successfully registered device to backendless");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Error registering device to sever. Reason: " + fault.getDetail());
                firstInitTasksManager.onSingleOperationFailed(getResources().getString(R.string.error_app_init));
            }
        });
    }
}
