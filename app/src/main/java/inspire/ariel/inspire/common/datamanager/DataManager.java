package inspire.ariel.inspire.common.datamanager;

import com.backendless.BackendlessUser;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.leader.Leader;
import lombok.Data;

@Data
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private static DataManager manager = null;
    private BackendlessUser user;
    private int messagesSize;

    public static DataManager getInstance() {
        if (manager == null) {
            manager = new DataManager();
            manager.initUser();
            manager.messagesSize = 0;
        }
        return manager;
    }

    private DataManager() {
    }

    private void initUser() {
        manager.user = new BackendlessUser();
        manager.user.setProperty(AppStrings.KEY_OBJECT_ID, AppStrings.VAL_LEADER_OBJECT_ID);
        manager.user.setProperty(AppStrings.KEY_NAME, AppStrings.VAL_LEADER_NAME);
        manager.user.setProperty(AppStrings.KEY_DESCRIPTION, AppStrings.VAL_LEADER_DESCRIPTION);
    }
}
