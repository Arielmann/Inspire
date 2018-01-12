package inspire.ariel.inspire.common.treatslist.model;

import android.content.Context;

import java.util.List;

import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;

public interface TreatListModel {

    List<Treat> getTreats();

    void setTreats(List<Treat> treats);

    void insertOrUpdateTreatsListToDb(List<Treat> treats);

    void syncRealmWithServerTreats(Context context, List<Treat> serverTreats, GenericOperationCallback callback);

    void syncPurchasedTreatsInDb(Context context, List<Treat> serverTreats, GenericOperationCallback callback);

    void updateTreatInDb(Treat treat);

    void updateTreatUserPurchasesInDb(Treat treat);

    void deleteAllTreatsFromDb();

    void deleteTreatFromDb(Treat treat);
}
