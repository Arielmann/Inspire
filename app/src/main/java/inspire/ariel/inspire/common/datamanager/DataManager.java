package inspire.ariel.inspire.common.datamanager;

import com.backendless.BackendlessUser;

import java.util.UUID;

import inspire.ariel.inspire.Leader;

public class DataManager {

    private BackendlessUser user;

    private static DataManager manager = null;
    private Leader leader;

    public static DataManager getInstance() {
        if(manager == null){
            manager = new DataManager();
            manager.leader = new Leader();
            manager.leader.setObjectId(UUID.randomUUID().toString());
            manager.leader.setName("Winston Churchill");
        }
        return manager;
    }

    private DataManager() {
    }

    public BackendlessUser getUser() {
        return user;
    }

    public void setUser(BackendlessUser user) {
        this.user = user;
    }

    public Leader getLeader() {
        return leader;
    }
}
