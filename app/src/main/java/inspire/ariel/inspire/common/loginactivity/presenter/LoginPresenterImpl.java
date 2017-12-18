package inspire.ariel.inspire.common.loginactivity.presenter;

import android.app.Activity;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.CallbackManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.loginactivity.view.LoginView;
import inspire.ariel.inspire.common.resources.ResourcesProvider;
import lombok.Getter;

public class LoginPresenterImpl implements LoginPresenter {

    @Inject
    @Getter CallbackManager fbCallbackManager;

    @Inject
    ResourcesProvider customResourcesProvider;

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
                        Log.i(TAG, "Successfully logged in startOperations user's id: " + loggedInUser.getUserId());
                        loginView.onUserLoggedIn();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "Login failed. Reason: " + fault.getDetail());
                        loginView.onServerOperationFailed(customResourcesProvider.getResources().getString(R.string.generic_error_login));
                    }
                }, true);
    }
}
