package inspire.ariel.inspire.common.datamanager;

import android.util.Log;

import com.backendless.BackendlessUser;

import lombok.Data;

@Data
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private static DataManager manager;
    private BackendlessUser user;
    private int messagesSize;
    private UserStatusData userStatusData;

    public static DataManager getInstance() {
        if (manager == null) {
            manager = new DataManager();
            manager.user = new BackendlessUser();
            manager.messagesSize = 0;
            manager.setUnauthorizedUser();
        }
        return manager;
    }

    public void setNormalUser(BackendlessUser user) {
        this.user = user;
        manager.userStatusData = UserStatusData.getNormalUserData();
        Log.i(TAG, "User was set at normal mode");
    }

    public void setAdminUser(BackendlessUser user) {
        this.user = user;
        manager.userStatusData = UserStatusData.getAdminDataUserData();
        Log.i(TAG, "User was set at ADMIN mode");
    }

    public void setUnauthorizedUser() {
        manager.userStatusData = UserStatusData.getAdminDataUserData();
        Log.i(TAG, "User was set at Unauthorized mode");
    }

    private DataManager() {
    }
}
