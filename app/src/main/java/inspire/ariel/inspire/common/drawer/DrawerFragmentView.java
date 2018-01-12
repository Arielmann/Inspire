package inspire.ariel.inspire.common.drawer;

import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.util.List;

import inspire.ariel.inspire.common.treatslist.view.TreatsListView;

public interface DrawerFragmentView {
    void init(TreatsListView treatListView);

    void addExtraDrawerItems(List<SecondaryDrawerItem> extraItems);
}
