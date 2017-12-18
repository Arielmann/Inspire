package inspire.ariel.inspire.common.utils.errorutils;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

import inspire.ariel.inspire.R;
import inspire.ariel.inspire.common.constants.AppStrings;
import lombok.Getter;

public class ErrorsManager {
    @Getter
    private final Map<String, String> errorsMap;
    private final Resources res;

    public ErrorsManager(Resources res) {
        errorsMap = new HashMap<>();
        this.res = res;
        initErrorsMap();
    }

    private void initErrorsMap() {
        errorsMap.put(AppStrings.BACKENDLESS_ERROR_CODE_INVALID_LOGIN_OR_PASSWORD, res.getString(R.string.error_invalid_login_or_password));
        errorsMap.put(AppStrings.BACKENDLESS_ERROR_CODE_EMPTY_PASSWORD_INPUT, res.getString(R.string.error_empty_password_input));
        errorsMap.put(AppStrings.BACKENDLESS_ERROR_CODE_API_CALLS_LIMIT_REACHED, res.getString(R.string.error_empty_api_calls_limit_reached));
    }

    public String getErrorFromFaultCode(String faultCode, String defaultError) {
        if (errorsMap.containsKey(faultCode)) {
            return errorsMap.get(faultCode);
        }
        return defaultError;
    }
}

