package inspire.ariel.inspire.common.datamanager;

import com.backendless.BackendlessUser;

import java.util.ArrayList;

import inspire.ariel.inspire.leader.Leader;
import lombok.Data;

@Data
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private BackendlessUser user;
    private static DataManager manager = null;
    private Leader leader;
    private int messagesSize;

    public static DataManager getInstance() {
        if(manager == null){
            manager = new DataManager();
            manager.leader = new Leader();
            manager.messagesSize = 0;
        }
        return manager;
    }

    private DataManager() {
    }

    public void setAllLeaderAttrsButQuotes(Leader leader){
        this.leader.setObjectId(leader.getObjectId());
        this.leader.setName(leader.getName());
        this.leader.setDescription(leader.getDescription());
        this.leader.setFollowers(leader.getFollowers());
    }
}
