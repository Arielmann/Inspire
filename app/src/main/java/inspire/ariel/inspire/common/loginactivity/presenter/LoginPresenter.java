package inspire.ariel.inspire.common.loginactivity.presenter;

import com.facebook.CallbackManager;

public interface LoginPresenter {
    void login();
    CallbackManager getFbCallbackManager();
}
