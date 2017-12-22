package inspire.ariel.inspire.common.utils.asyncutils;

import com.orhanobut.hawk.Hawk;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.operationsutils.GenericOperationCallback;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Not Thread Safe
 */
@RequiredArgsConstructor
public class CodependentTasksManager {
    @Getter
    final GenericOperationCallback callback;
    private final int mustSucceedOperations;
    private int alreadySucceeded;
    private boolean alreadyFailed;

    public void onSingleOperationSuccessful() {
        alreadySucceeded++;
        if (alreadySucceeded >= mustSucceedOperations) {
            callback.onSuccess();
        }
    }

    public void onSingleOperationFailed(String reason) {
        if (alreadyFailed) {
            callback.onFailure(AppStrings.EMPTY_STRING); //prevent another error popup
        }else {
            alreadyFailed = true;
            callback.onFailure(reason);
        }
    }

}