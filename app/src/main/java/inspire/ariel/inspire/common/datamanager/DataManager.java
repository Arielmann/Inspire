package inspire.ariel.inspire.common.datamanager;

import com.backendless.BackendlessUser;

import java.util.UUID;

import inspire.ariel.inspire.leader.Leader;
import lombok.Data;

@Data
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();

    private BackendlessUser user;
    private static DataManager manager = null;
    private Leader leader;

    public static DataManager getInstance() {
        if(manager == null){
            manager = new DataManager();
        }
        return manager;
    }

    private DataManager() {
    }
}
