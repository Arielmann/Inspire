package inspire.ariel.inspire.common.datamanager;

import android.util.Log;

import com.backendless.BackendlessUser;

import lombok.Getter;
import lombok.Setter;

public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private static DataManager manager;
   @Getter private BackendlessUser user;
    @Setter @Getter private int messagesSize;
    @Getter private UserStatusData userStatusData;

    public static DataManager getInstance() {
        if (manager == null) {
            manager = new DataManager();
            manager.user = new BackendlessUser();
            manager.messagesSize = 0;
            manager.setUnauthorizedUserStatus();
        }
        return manager;
    }

    public void setNormalUserStatus(BackendlessUser user) {
        this.user = user;
        manager.userStatusData = UserStatusData.getNormalUserData();
        Log.i(TAG, "User was set at normal mode");
    }

    public void setAdminUserStatus(BackendlessUser user) {
        this.user = user;
        manager.userStatusData = UserStatusData.getAdminDataUserData();
        Log.i(TAG, "User was set at ADMIN mode");
    }

    public void setUnauthorizedUserStatus() {
        manager.userStatusData = UserStatusData.getAdminDataUserData();
        Log.i(TAG, "User was set at Unauthorized mode");
    }

    private DataManager() {
    }
}
