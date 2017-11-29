package inspire.ariel.inspire.common.quoteslist.presenter;

import android.content.Intent;

public interface QuoteListPresenter {

    void onDestroy();

    void OnNewIntent(Intent intent);
}
