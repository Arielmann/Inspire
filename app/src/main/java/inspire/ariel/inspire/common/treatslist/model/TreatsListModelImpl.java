package inspire.ariel.inspire.common.treatslist.model;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.di.AppComponent;
import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.localdbmanager.RealmManager;
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
    @Setter
    @Getter
    private List<Treat> treatsInAdapter;

    public static TreatsListModelImpl getInstance(AppComponent component) {
        if (model == null) {
            model = new TreatsListModelImpl();
            component.inject(model);
            model.treatsInAdapter = new ArrayList<>();
            if (Hawk.contains(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER)) {
                if (!((boolean) Hawk.get(AppStrings.KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER))) {
                    model.treatsInAdapter = model.realmManager.getTreats();
                }
            }
        }
            return model;
    }

    private TreatsListModelImpl() {
        }

        @Override
        public void saveTreatToDb (Treat treat){
            realmManager.saveTreat(treat);
        }

        @Override
        public void saveTreatsListToDb (List < Treat > treats) {
            realmManager.saveTreatsList(treats);
        }

        @Override
        public void syncRealmWithServerTreats (List < Treat > serverTreats) {
            realmManager.syncRealmWithServerTreats(serverTreats);
        }

        @Override
        public void deleteTreatFromDb (Treat treat){
            realmManager.deleteTreat(treat);
        }

        @Override
        public void updateTreatInDb (Treat treat){
            realmManager.updateTreat(treat);
        }

        @Override
        public void removeAllTreatsFromDb () {
            realmManager.deleteAllTreats();
        }

        @Override
        public void updatePurchasedTreatsInDb (List < Treat > serverTreats) {
            realmManager.updatePurchasedTreatsInDb(serverTreats);
        }

        @Override
        public List<Treat> getTreatsFromDb () {
            return realmManager.getTreats();
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
