package inspire.ariel.inspire.common.treatslist.presenter;

import android.content.Intent;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapterPresenter;
import io.realm.RealmResults;

public interface TreatsListPresenter {

    void startOperations(TreatListAdapterPresenter adapterPresenter);

    void onDestroy();

    void OnNewIntent(Intent intent);

    void logout();

    void login(CharSequence password);

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollChangedListener();

    //Called when treat was updated successfully
    void onTreatUpdated(Intent data);

    void deleteTreat(int treatPosition);

}
