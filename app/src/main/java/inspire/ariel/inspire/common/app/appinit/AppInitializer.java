package inspire.ariel.inspire.common.app.appinit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import javax.inject.Inject;

import inspire.ariel.inspire.common.app.InspireApplication;
import inspire.ariel.inspire.common.constants.AppInts;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.resources.ResourcesInitializer;
import inspire.ariel.inspire.common.resources.ResourcesProviderImpl;

public class AppInitializer {

    @Inject
    ResourcesInitializer resourcesInitializer;

    private static final String TAG = AppInitializer.class.getSimpleName();

    public void InitApp(final InspireApplication application) {
        Backendless.initApp(application, AppStrings.BACKENDLESS_APPLICATION_ID, AppStrings.BACKENDLESS_API_KEY);

        //TODO: use realm instead of SharedPrefManager

        registerDeviceToBackendless(application, "");
        registerDeviceToBackendless(application, AppStrings.LEADER_NAME);
        application.getAppComponent().inject(this);
        resourcesInitializer.init(application);
    }

    private static void registerDeviceToBackendless(final Context context, String channel) {
        Backendless.Messaging.registerDevice(AppStrings.SENDER_ID, channel, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Log.d(TAG, "Successfully registered device to backendless");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(context, "There was an error registering your device to the app, please restart and try again in a few minutes", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error registering device: " + fault.getMessage());
            }
        });
    }
}