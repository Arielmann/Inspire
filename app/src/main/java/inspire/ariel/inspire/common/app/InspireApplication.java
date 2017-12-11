package inspire.ariel.inspire.common.app;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.di.AppModule;
import inspire.ariel.inspire.common.di.DaggerAppComponent;
import inspire.ariel.inspire.common.di.ListsModule;
import inspire.ariel.inspire.common.di.ModelsModule;
import inspire.ariel.inspire.common.di.NetworkModule;
import inspire.ariel.inspire.common.di.ResourcesModule;
import inspire.ariel.inspire.common.resources.ResourcesInitializer;
import inspire.ariel.inspire.common.utils.backendutils.NetworkHelper;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class InspireApplication extends Application {

    @Inject
    ResourcesInitializer resourcesInitializer;

    @Inject
    Calendar calendar;

    private static final String TAG = InspireApplication.class.getSimpleName();
    private AppComponent appComponent;
    private MultipleCoDependentTaskManager firstInitTasksManager;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .modelsModule(new ModelsModule())
                .resourcesModule(new ResourcesModule(getResources(), getAssets()))
                .listsModule(new ListsModule())
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void initApp(GenericOperationCallback operationCallback) {
        if (NetworkHelper.getInstance().hasNetworkAccess(this)) {
            this.getAppComponent().inject(this);
            Hawk.init(this).build();
            Backendless.initApp(this, AppStrings.BACKENDLESS_VAL_APPLICATION_ID, AppStrings.BACKENDLESS_VAL_API_KEY);
            resourcesInitializer.init(this);
            FontsManager.getInstance().init(this);
            if (!Hawk.contains(AppStrings.IS_FIRST_LAUNCH)) {
                firstInitTasksManager = new MultipleCoDependentTaskManager(operationCallback, AppNumbers.MUST_COMPLETED_TASKS_ON_FIRST_LAUNCH);
                initFirstLaunch();
            } else {
                operationCallback.onSuccess();
            }
        } else {
            operationCallback.onFailure(getResources().getString(R.string.error_no_connection));
        }
    }

    /**NOTE:
     * Don't forget to increase firstInitTasksManager's "mustSucceedOperations"
     * if more required operations are added to this method
     */
    private void initFirstLaunch() {
        willRegisterDeviceToBackendless();
        determineIfUserLeader();
    }

    private void willRegisterDeviceToBackendless() {
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, AppNumbers.BACKENDLESS_REGISTRATION_CHANNEL_DAYS_LIMIT);
        List<String> channels = new ArrayList<String>() {{
            add(AppStrings.BACKENDLESS_DEFAULT_CHANNEL);
            add(AppStrings.VAL_OWNER_NAME);
        }};
        registerDeviceToBackendless(channels, calendar.getTime());
    }

    //TODO: Register one time only
    private void registerDeviceToBackendless(List<String> channels, Date expirationDate) {
        Hawk.put(AppStrings.KEY_IS_USER_OWNER, false);

        Backendless.Messaging.registerDevice(AppStrings.VAL_SENDER_ID, channels, expirationDate, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                firstInitTasksManager.onSingleOperationSuccessful();
                Log.d(TAG, "Successfully registered device to backendless");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                firstInitTasksManager.onSingleOperationFailed(fault.getDetail());
            }
        });
    }

    private void determineIfUserLeader() {
        Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
            @Override
            public void handleResponse(DeviceRegistration response) {
                if (response.getDeviceId().equals(AppStrings.BACKENDLESS_VAL_OWNER_DEVICE_ID)) {
                    Hawk.put(AppStrings.KEY_IS_USER_OWNER, true);
                    firstInitTasksManager.onSingleOperationSuccessful();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "An error occurred while checking if this device belongs to the leader. reason: " + fault.getDetail());
                firstInitTasksManager.onSingleOperationFailed(fault.getDetail());
            }
        });
    }

    @RequiredArgsConstructor
    private class MultipleCoDependentTaskManager {
        @Getter final GenericOperationCallback callback;
        private final int mustSucceedOperations;
        private int alreadySucceeded;

        protected void onSingleOperationSuccessful() {
            alreadySucceeded++;
            if (alreadySucceeded == mustSucceedOperations) {
                Hawk.put(AppStrings.IS_FIRST_LAUNCH, false);
                callback.onSuccess();
            }
        }

        protected void onSingleOperationFailed(String reason) {
            callback.onFailure(reason);
        }

    }
}
