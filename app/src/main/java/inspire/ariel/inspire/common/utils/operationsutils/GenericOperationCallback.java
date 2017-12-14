package inspire.ariel.inspire.common.utils.operationsutils;

public interface GenericOperationCallback {

    void onSuccess();

    void onFailure(String errorForUser);
}
