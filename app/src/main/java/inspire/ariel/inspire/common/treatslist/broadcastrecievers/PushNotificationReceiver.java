package inspire.ariel.inspire.common.treatslist.broadcastrecievers;

import com.backendless.push.BackendlessBroadcastReceiver;
import com.backendless.push.BackendlessPushService;

import inspire.ariel.inspire.common.treatslist.services.PushNotificationService;

public class PushNotificationReceiver extends BackendlessBroadcastReceiver{

    public PushNotificationReceiver() {
        super();
    }

    @Override
    public Class<? extends BackendlessPushService> getServiceClass() {
        return PushNotificationService.class;
    }
}