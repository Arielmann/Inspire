package inspire.ariel.inspire.common.treatslist.model;

import java.util.ArrayList;
import java.util.List;

import inspire.ariel.inspire.common.treatslist.Treat;
import lombok.Getter;
import lombok.Setter;

public class TreatsListModelImpl implements TreatListModel {

    /*
    * This singleton model holds the data related with
    * the treats presented to the user.
    */

    private static TreatsListModelImpl model;
    @Getter @Setter private List<Treat> treats;

    public static TreatsListModelImpl getInstance() {
        if (model == null) {
            model = new TreatsListModelImpl();
            model.treats = new ArrayList<>();
        }
        return model;
    }

    private TreatsListModelImpl() {
    }
}


