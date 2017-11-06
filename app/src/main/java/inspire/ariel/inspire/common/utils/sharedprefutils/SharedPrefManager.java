package inspire.ariel.inspire.common.utils.sharedprefutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.HashMap;

import inspire.ariel.inspire.common.constants.AppStrings;
import inspire.ariel.inspire.common.utils.imageutils.ImageUtils;

public class SharedPrefManager {

    /*
    * Shared preferences of this app saves profile info and has
    * id
    * name
    * password
    * email
    * location
    * description
    * profile image file path
    * profile image url
    * gcm token
    * device screen's height
    * device screen's width
    *
    * these properties will be gettable throughout this class
    * */

    public enum SharedPrefProperty {
        ID(AppStrings.ID),
        NAME(AppStrings.NAME),
        EMAIL(AppStrings.EMAIL),
        IS_FIRST_LAUNCH(AppStrings.IS_FIRST_LAUNCH),
        PROFILE_IMAGE_PATH(AppStrings.PROFILE_IMAGE_URL);

        private String propertyName;

        private String getName(){
            return propertyName;
        };

        SharedPrefProperty(String propertyName) {
            this.propertyName = propertyName;
        }
    }

    // private static ImageArray sharedPrefs = new ImageArray();
    private static SharedPrefManager instance;
    private SharedPreferences insideSharedPref;
    private int notificationsCounter;
    private Bitmap userImageBitmap = ImageUtils.defaultProfileImage;


    //creates the only instance
    private SharedPrefManager(SharedPreferences sharedPreferences) {
        insideSharedPref = sharedPreferences;
    }

    // prevents creating of instances
    public static SharedPrefManager getInstance(Context context) { // create a static common database
        if (instance == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Account info", Context.MODE_PRIVATE);
            instance = new SharedPrefManager(sharedPreferences);
        }
        return instance;
    }

    public SharedPreferences getSharedPrefs() {
        return insideSharedPref;
    }

    public boolean isFirstLaunch() {
        return insideSharedPref.getBoolean(SharedPrefProperty.IS_FIRST_LAUNCH.getName(), true);
    }

    public int getUserId() {
        return insideSharedPref.getInt("id", -1);
    }

    public String getUserName() {
        return insideSharedPref.getString("name", "user name error");
    }

    public String getUserPassword() {
        return insideSharedPref.getString("password", "password error");
    }

    public String getUserLocation() {
        return insideSharedPref.getString("location", "location error");
    }

    public String getProfileImagePath() {
        return insideSharedPref.getString("profileImageFilePath", "profile image file path error");
    }

    public String getProfileImageUrl() {
        return insideSharedPref.getString("profileImageUrl", "profile image url error");
    }

    public String getUserDescription() {
        return insideSharedPref.getString("description", "description error");
    }

    public String getUserGcmToken() {
        return insideSharedPref.getString("gcmToken", "gcm token error");
    }

    public int getUserDeviceScreenHeight() {
        return insideSharedPref.getInt("deviceScreenHeight", -1);
    }

    public int getUserDeviceScreenWidth() {
        return insideSharedPref.getInt("deviceScreenWidth", -1);
    }

    public Bitmap getUserImageBitmap() {
        return userImageBitmap;
    }

    public void setUserImageBitmap(Bitmap userImageBitmap) {
        this.userImageBitmap = userImageBitmap;
    }

    public void saveStringInfoToSharedPreferences(final SharedPrefProperty property, final String value) {
        AsyncTask saveToSharedPref = new AsyncTask() {

            @Override
            protected Object doInBackground(Object... params) {
                SharedPreferences.Editor editor = insideSharedPref.edit()    //SharedPrefManager is a LOCAL class
                        .putString(property.getName(), value);
                editor.apply();
                return null;
            }
        };
        saveToSharedPref.execute();
    }


    public void saveIntInfoToSharedPreferences(final SharedPrefProperty property, final int value) {
        AsyncTask saveToSharedPref = new AsyncTask() {

            @Override
            protected Object doInBackground(Object... params) {
                SharedPreferences.Editor editor = insideSharedPref.edit()    //SharedPrefManager is a LOCAL class
                        .putInt(property.getName(), value);
                editor.apply();
                return null;
            }
        };
        saveToSharedPref.execute();
    }

    public void saveBooleanToSharedPreferences(final SharedPrefProperty property, final boolean value) {
        AsyncTask saveToSharedPref = new AsyncTask() {

            @Override
            protected Object doInBackground(Object... params) {
                SharedPreferences.Editor editor = insideSharedPref.edit()    //SharedPrefManager is a LOCAL class
                        .putBoolean(property.getName(), value);
                editor.apply();
                return null;
            }
        };
        saveToSharedPref.execute();
    }

    public HashMap<String, String> initSharedPrefData() {
        HashMap<String, String> sharedPrefData = new HashMap<>();
        sharedPrefData.put("name", insideSharedPref.getString("name", "error"));
        sharedPrefData.put("password", insideSharedPref.getString("password", "error"));
        //sharedPrefData.put("email",  insideSharedPref.getString("email", "error")); //Not yet active
        sharedPrefData.put("location", insideSharedPref.getString("location", "error"));
        sharedPrefData.put("profileImageFilePath", insideSharedPref.getString("profileImageFilePath", "error"));
        sharedPrefData.put("profileImageUrl", insideSharedPref.getString("profileImageUrl", "error"));
        sharedPrefData.put("description", insideSharedPref.getString("description", "error"));
        sharedPrefData.put("gcmToken", insideSharedPref.getString("gcmToken", "error"));
        return sharedPrefData;
    }

    public int getNotificationsCounter() {
        return notificationsCounter++;
    }

    public String getProfileImagesDir() {
        return "/Make_Me_Beautiful/Contacts";
    }

    @Override
    public String toString() {
        return "Leader Details: " +
                " Name: " + getUserName() +
                " Location: " + getUserLocation() +
                " Description: " + getUserDescription() +
                " Profile image file: " + getProfileImagePath() +
                " Profile image url: " + getProfileImageUrl() +
                " Gcm Token: " + getUserGcmToken() +

                " Device details: " +
                " Screen Height: " + getUserDeviceScreenHeight() +
                " Screen Width: " + getUserDeviceScreenWidth();
    }
}

