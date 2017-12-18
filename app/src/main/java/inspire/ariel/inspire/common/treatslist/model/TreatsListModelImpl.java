package inspire.ariel.inspire.common.treatslist.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.dbmanager.RealmManager;
import lombok.Getter;
import lombok.Setter;

public class TreatsListModelImpl implements TreatListModel {

    /*
    * This singleton model holds the data related with
    * the treatsFromDb presented to the user.
    */

    @Inject
    RealmManager realmManager;

    private static TreatsListModelImpl model;
    @Setter @Getter private List<Treat> treatsInAdapter;

    public static TreatsListModelImpl getInstance(AppComponent component) {
        if (model == null) {
            model = new TreatsListModelImpl();
            component.inject(model);
            model.treatsInAdapter = model.realmManager.getTreats();
        }
        return model;
    }

    private TreatsListModelImpl() {
    }

    @Override
    public void saveTreatToDb(Treat treat) {
        realmManager.saveTreat(treat);
    }

    @Override
    public void saveTreatsListToDb(List<Treat> treats) {
        realmManager.saveTreatsList(treats);
    }

    @Override
    public void syncRealmWithServerTreats(List<Treat> serverTreats) {
        realmManager.syncRealmWithServerTreats(serverTreats);
    }

    @Override
    public void removeTreatFromDb(Treat treat) {
        realmManager.removeTreat(treat);
    }

    @Override
    public void updateTreatInDb(Treat treat) {
        realmManager.updateTreat(treat);
    }
}


