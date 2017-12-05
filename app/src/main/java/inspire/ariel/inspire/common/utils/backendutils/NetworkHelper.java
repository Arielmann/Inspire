package inspire.ariel.inspire.common.utils.backendutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {

    private static NetworkHelper insideNetworkHelper;


    //creates the only instance
    private NetworkHelper() {}

    // prevents creating of instances
    public static NetworkHelper getInstance() { // create a static common database
        if (insideNetworkHelper == null) {
            insideNetworkHelper = new NetworkHelper();
        }
        return insideNetworkHelper;
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
