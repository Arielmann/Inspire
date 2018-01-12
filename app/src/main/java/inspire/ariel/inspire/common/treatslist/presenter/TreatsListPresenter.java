package inspire.ariel.inspire.common.treatslist.presenter;

import android.content.Intent;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.Treat;
import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapterPresenter;

public interface TreatsListPresenter {

    void prepareForFirstLaunchIfNeeded();

    void startOperations(TreatListAdapterPresenter adapterPresenter);

    void onDestroy();

    void OnNewIntent(Intent intent);

    void logout();

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollChangedListener();

    //Called when treat was updated successfully
    void onTreatUpdated(Intent data);

    void setTreatUnPurchaseable(int treatPosition);

    void purchaseTreat(String adminPassword, Treat treat, int treatPosition);
}
