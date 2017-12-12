package inspire.ariel.inspire.common.treatslist.model;

import java.util.List;

import inspire.ariel.inspire.common.treatslist.Treat;

public interface TreatListModel {
    List<Treat> getTreats();
    void setTreats(List<Treat> treats);
}
