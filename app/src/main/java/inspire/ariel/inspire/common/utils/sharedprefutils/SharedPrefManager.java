package inspire.ariel.inspire.common.utils.sharedprefutils;

import android.content.Context;
import android.content.SharedPreferences;

import inspire.ariel.inspire.common.constants.AppStrings;

public class SharedPrefManager {

    public enum SharedPrefProperty {
        IS_FIRST_LAUNCH(AppStrings.KEY_IS_FIRST_LAUNCH),
        DEVICE_SCREEN_HEIGHT(AppStrings.KEY_DEVICE_STRING_HEIGHT),
        DEVICE_SCREEN_WIDTH(AppStrings.KEY_DEVICE_STRING_WIDTH);

        private String propertyName;

        private String getName(){
            return propertyName;
        };

        SharedPrefProperty(String propertyName) {
            this.propertyName = propertyName;
        }
    }

    private static SharedPrefManager manager;
    private SharedPreferences insideSharedPref;

    private SharedPrefManager(SharedPreferences sharedPreferences) {
        insideSharedPref = sharedPreferences;
    }

    public static SharedPrefManager getInstance(Context context) {
        if (manager == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(AppStrings.ACCOUNT_INFO, Context.MODE_PRIVATE);
            manager = new SharedPrefManager(sharedPreferences);
        }
        return manager;
    }


    public int getUserDeviceScreenHeight() {
        return insideSharedPref.getInt(SharedPrefProperty.DEVICE_SCREEN_HEIGHT.getName(), 150);
    }

    public int getUserDeviceScreenWidth() {
        return insideSharedPref.getInt(SharedPrefProperty.DEVICE_SCREEN_WIDTH.getName(), 150);
    }

    public boolean isFirstLaunch() {
        return insideSharedPref.getBoolean(SharedPrefProperty.IS_FIRST_LAUNCH.getName(), true);
    }

    public void saveStringInfoToSharedPreferences(final SharedPrefProperty property, final String value) {
        Thread thread = new Thread(() -> {
            SharedPreferences.Editor editor = insideSharedPref.edit()
                    .putString(property.getName(), value);
            editor.apply();
        });

        thread.run();
    }


    public void saveIntInfoToSharedPreferences(final SharedPrefProperty property, final int value) {
        Thread thread = new Thread(() -> {
            SharedPreferences.Editor editor = insideSharedPref.edit()
                    .putInt(property.getName(), value);
            editor.apply();
        });

        thread.run();;
    }

    public void saveBooleanToSharedPreferences(final SharedPrefProperty property, final boolean value) {

        Thread thread = new Thread(() -> {
            SharedPreferences.Editor editor = insideSharedPref.edit()
                    .putBoolean(property.getName(), value);
            editor.apply();
        });

        thread.run();

    }

}

