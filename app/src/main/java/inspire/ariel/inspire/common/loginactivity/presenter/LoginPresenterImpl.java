package inspire.ariel.inspire.common.loginactivity.presenter;

import android.app.Activity;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.CallbackManager;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.loginactivity.view.LoginView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import inspire.ariel.inspire.common.utils.errorutils.ErrorsManager;
import lombok.Getter;

public class LoginPresenterImpl implements LoginPresenter {

    @Inject
    @Getter CallbackManager fbCallbackManager;

    @Inject ResourcesProvider customResourcesProvider;

    @Inject
    ErrorsManager errorsManager;

    private static final String TAG = LoginPresenter.class.getName();
    private LoginView loginView;

    public LoginPresenterImpl(AppComponent component, LoginView loginView) {
        component.inject(this);
        this.loginView = loginView;
    }

    /**
     * Facebook
     */
    @Override
    public void login() {
      /*  Map<String, String> facebookFieldMappings = new HashMap<String, String>() {{
            put(AppStrings.KEY_EMAIL, AppStrings.BACKENDLESS_VAL_FB_EMAIL);
        }};
        List<String> permissions = new ArrayList<String>() {{
            add(AppStrings.KEY_EMAIL);
        }};*/

        Backendless.UserService.loginWithFacebookSdk((Activity) loginView, fbCallbackManager,
                new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser loggedInUser) {
                        Log.i(TAG, "Successfully logged in startOperations user's id: " + loggedInUser.toString());
                        BackendlessUser previousUser = Hawk.get(AppStrings.KEY_LOGGED_IN_USER);
                        Hawk.put(AppStrings.KEY_LOGGED_IN_USER, loggedInUser);
                        if (previousUser.getObjectId() != null && !previousUser.getObjectId().equalsIgnoreCase(loggedInUser.getObjectId())) {
                            Hawk.put(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER, false); //Same user logged in again
                        }
                        Hawk.put(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER, true);
                        loginView.onUserLoggedIn();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Login failed. Reason: " + fault.getDetail());
                        String errorForUser = errorsManager.getErrorFromFaultCode(fault.getCode(), customResourcesProvider.getResources().getString(R.string.generic_error_login));
                        loginView.onServerOperationFailed(errorForUser);
                    }
                }, true);
    }
}
