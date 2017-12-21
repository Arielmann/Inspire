package inspire.ariel.inspire.common.treatslist.presenter;

import android.content.Intent;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.treatslist.Treat;
import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapterPresenter;

public interface TreatsListPresenter {

    void startOperations(TreatListAdapterPresenter adapterPresenter);

    void onDestroy();

    void OnNewIntent(Intent intent);

    void logout();

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollChangedListener();

    //Called when treat was updated successfully
    void onTreatUpdated(Intent data);

    void deleteTreat(int treatPosition);

    void purchaseTreat(CharSequence adminPassword, Treat treat, int treatPosition);
}
