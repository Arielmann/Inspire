package inspire.ariel.inspire.common.quoteslist.view;

import android.util.Log;

public interface ContinuousOperationCallback {
   default void onSuccess(){Log.d(ContinuousOperationCallback.class.getName(), "call completed successfully");}
    void onFailure();
}
