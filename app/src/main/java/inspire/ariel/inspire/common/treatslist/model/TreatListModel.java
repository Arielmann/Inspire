package inspire.ariel.inspire.common.treatslist.model;

import java.util.List;

import inspire.ariel.inspire.common.treatslist.Treat;

public interface TreatListModel {

    List<Treat> getTreatsInAdapter();

    void setTreatsInAdapter(List<Treat> treats);

    void saveTreatToDb(Treat newTreat);

    void saveTreatsListToDb(List<Treat> treats);

    void syncRealmWithServerTreats(List<Treat> serverTreats);

    void updateTreatInDb(Treat treat);

    void deleteAllTreatsFromDb();

    void updatePurchasedTreatsInDb(List<Treat> serverTreats);

    List<Treat> getVisibleTreatsFromDb();

    void deleteTreatFromDb(Treat treat);
}
