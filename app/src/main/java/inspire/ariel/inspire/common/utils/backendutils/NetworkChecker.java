package inspire.ariel.inspire.common.utils.backendutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChecker {

    private static NetworkChecker insideNetworkChecker;


    //creates the only instance
    private NetworkChecker() {}

    // prevents creating of instances
    public static NetworkChecker getInstance() { // create a static common database
        if (insideNetworkChecker == null) {
            insideNetworkChecker = new NetworkChecker();
        }
        return insideNetworkChecker;
    }

    public boolean hasNetworkAccess(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = null;
            if (manager != null) {
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }catch(Exception e){
            return false;
        }

    }

}
