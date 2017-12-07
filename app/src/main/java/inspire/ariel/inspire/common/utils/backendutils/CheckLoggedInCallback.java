package inspire.ariel.inspire.common.utils.backendutils;

public interface CheckLoggedInCallback {

    void onUserStatusReceived(boolean isLoggedIn);
    void onFailure(String reason);
}
