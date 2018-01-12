package inspire.ariel.inspire.common.treatslist.model;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.localdbmanager.RealmManager;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import lombok.Getter;
import lombok.Setter;

public class TreatsListModelImpl implements TreatListModel {

    /*
    * This singleton model holds the data related with
    * the treatsFromDb presented to the user.
    */

    private static final String TAG = TreatsListModelImpl.class.getName();

    @Inject
    RealmManager realmManager;

    private static TreatsListModelImpl model;
    @Setter @Getter private List<Treat> treats;

    public static TreatsListModelImpl getInstance(AppComponent component) {
        if (model == null) {
            model = new TreatsListModelImpl();
            component.inject(model);
            model.treats = new ArrayList<>();
            if (!((boolean) Hawk.get(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER))) {
                model.treats = model.realmManager.getVisibleTreats();
            }
        }
        return model;
    }

    private TreatsListModelImpl() {
    }

    public void insertOrUpdateTreatsListToDb(List<Treat> treats) {
        realmManager.insertOrUpdateTreatsList(treats);
    }

    @Override
    public void syncRealmWithServerTreats(Context context, List<Treat> serverTreats, GenericOperationCallback networkOperationsCallback) {
        realmManager.syncRealmWithServerTreats(context, serverTreats, treats, new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                model.treats = realmManager.getVisibleTreats();
                networkOperationsCallback.onSuccess();
            }

            @Override
            public void onFailure(String errorForUser) {
                networkOperationsCallback.onFailure(errorForUser);
            }
        });
    }

    @Override
    public void syncPurchasedTreatsInDb(Context context, List<Treat> serverTreats, GenericOperationCallback netWorkOperationsCallback) {
        realmManager.syncPurchasedTreatsInDb(context, serverTreats, new GenericOperationCallback() {
            @Override
            public void onSuccess() {
                model.treats = realmManager.getVisibleTreats();
                netWorkOperationsCallback.onSuccess();
            }

            @Override
            public void onFailure(String errorForUser) {
                netWorkOperationsCallback.onFailure(errorForUser);
            }
        });
    }

    @Override
    public void updateTreatInDb(Treat treat) {
        realmManager.updateTreat(treat);
    }

    @Override
    public void updateTreatUserPurchasesInDb(Treat treat) {
        realmManager.updateTreatUserPurchasesColumn(treat);
    }

    @Override
    public void deleteAllTreatsFromDb() {
        realmManager.deleteAllTreats();
    }

    @Override
    public void deleteTreatFromDb(Treat treat) {
        realmManager.deleteTreat(treat);
    }
}



 /*   private void validateDataIntegrity(List<Treat> treats){
        Iterator<Treat> iter = treats.iterator();
        while (iter.hasNext()){
            Treat treat = iter.next();
            if(treat.getObjectId() == null){
                Log.e(TAG, "Null treat found in model. removing it. Check your data base logic");
                iter.remove();
            }
        }
    }*/
