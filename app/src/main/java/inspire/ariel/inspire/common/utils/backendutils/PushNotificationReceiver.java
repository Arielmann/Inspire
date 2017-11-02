package inspire.ariel.inspire.common.utils.backendutils;

import com.backendless.push.BackendlessBroadcastReceiver;
import com.backendless.push.BackendlessPushService;

public class PushNotificationReceiver extends BackendlessBroadcastReceiver{

    public PushNotificationReceiver() {
        super();
    }

    @Override
    public Class<? extends BackendlessPushService> getServiceClass() {
        return PushNotificationService.class;
    }
}