package inspire.ariel.inspire.owner.treatdesigner.view.treatdesigneractivity;

import inspire.ariel.inspire.common.Treat;

public class TreatsCreatorActivity extends AbstractTreatDesignerActivity {

    @Override
    void requestTreatPost() {
        Treat newTreat = super.createTreatForPost();
        presenter.requestPostTreat(newTreat);
    }
}
