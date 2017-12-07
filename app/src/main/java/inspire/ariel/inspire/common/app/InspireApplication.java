package inspire.ariel.inspire.common.app;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

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

public class InspireApplication extends Application {

    @Inject
    ResourcesInitializer resourcesInitializer;

    @Inject
    Calendar calendar;

    private static final String TAG = InspireApplication.class.getSimpleName();
    private AppComponent appComponent;

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

    /**
     * Warning: do not willInject with this component from:
     * QuoteCreatorActivity
     */
    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void initApp(GenericOperationCallback operationCallback) {
        if (NetworkHelper.getInstance().hasNetworkAccess(this)) {
            this.getAppComponent().inject(this);
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, AppNumbers.BACKENDLESS_REGISTRATION_CHANNEL_DAYS_LIMIT);
            Backendless.initApp(this, AppStrings.BACKENDLESS_VAL_APPLICATION_ID, AppStrings.BACKENDLESS_VAL_API_KEY);
            resourcesInitializer.init(this);
            FontsManager.getInstance().init(this);
            List<String> channels = new ArrayList<String>() {{
                add(AppStrings.BACKENDLESS_DEFAULT_CHANNEL);
                add(AppStrings.VAL_LEADER_NAME);
            }};
            registerDeviceToBackendless(channels, calendar.getTime(), operationCallback);

        } else {
            operationCallback.onFailure(getResources().getString(R.string.error_no_connection));
        }
    }

    //TODO: Register one time only
    private void registerDeviceToBackendless(List<String> channels, Date expirationDate, GenericOperationCallback operationCallback) {

        Backendless.Messaging.registerDevice(AppStrings.VAL_SENDER_ID, channels, expirationDate, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                operationCallback.onSuccess();
                Log.d(TAG, "Successfully registered device to backendless");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                operationCallback.onFailure(fault.getDetail());
            }
        });
    }
}
