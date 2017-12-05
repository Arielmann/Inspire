package inspire.ariel.inspire.common.app;

import android.app.Application;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.IDataStore;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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
import inspire.ariel.inspire.common.utils.callbackutils.GenericOperationCallback;
import inspire.ariel.inspire.common.utils.fontutils.FontsManager;
import inspire.ariel.inspire.leader.Leader;

public class InspireApplication extends Application {

    @Inject
    ResourcesInitializer resourcesInitializer;

    @Inject
    @Named(AppStrings.BACKENDLESS_TABLE_LEADER)
    IDataStore<Leader> leadersStorage;

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
            //logOut();
            registerDeviceToBackendless(channels, calendar.getTime(), operationCallback);

        } else {
            operationCallback.onFailure();
        }
    }

    private void loginUser(){
        Backendless.UserService.login("james.bond@mi6.co.uk", "iAmWatchingU", new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser user) {
                Log.i(TAG, "Login successful for user " + user.getObjectId());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "Login failed. reason: " + fault.getDetail());
            }
        }, true);
    }

    private void logOut(){

                Backendless.UserService.logout( new AsyncCallback<Void>() {
                    public void handleResponse( Void response )
                    {
                        // user has been logged out.
                    }

                    public void handleFault( BackendlessFault fault ) {
                        // something went wrong and logout failed, to get the error code call fault.getCode()
                    }
                });
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
                operationCallback.onFailure();
                Log.d(TAG, "Error registering device: " + fault.getDetail());
            }
        });
    }
}
