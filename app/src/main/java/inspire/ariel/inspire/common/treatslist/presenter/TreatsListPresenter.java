package inspire.ariel.inspire.common.treatslist.presenter;

import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import inspire.ariel.inspire.common.treatslist.adapter.TreatListAdapterPresenter;

public interface TreatsListPresenter {

    void init(TreatListAdapterPresenter adapterPresenter);

    void onDestroy();

    void OnNewIntent(Intent intent);

    void logout();

    void login(CharSequence password);

    DiscreteScrollView.ScrollStateChangeListener<?> getOnScrollChangedListener();

    //Called when treat was updated successfully
    void onTreatUpdated(Intent data);

    void deleteTreat(int treatPosition);

    CallbackManager getFbCallbackManager();

}
