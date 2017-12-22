package inspire.ariel.inspire.common.constants;

import java.util.List;

import inspire.ariel.inspire.BuildConfig;

public class AppStrings {

    /**
    * Static constants class which contains all strings that are NOT directly presented
    * to the user.
    * All user presented strings are located on R.string in the res package
    */

    //Tags
    public static final String REALM_SYNC_SERVER_TAG = "Data bases sync message";
    public static final String KEY_LOCAL_TREATS = "localTreats";

    //Backendless
    public static final String BACKENDLESS_VAL_APPLICATION_ID = "1C0DA01D-01F8-CC77-FF89-629A70D7CB00";
    public static final String BACKENDLESS_VAL_API_KEY = "D5242878-29DD-0377-FF39-FC1E91E64300";
    public static final String BACKENDLESS_VAL_FB_EMAIL = "fb_email";
    public static final String BACKENDLESS_VAL_OWNER_ID = "BBCF673D-BD9D-081B-FF09-364F223A1100";

    public static final String BACKENDLESS_TABLE_TREATS = "Treat";
    public static final String BACKENDLESS_TABLE_USERS = "Users";
    public static final String BACKENDLESS_TABLE_USER_COLUMN_TREATS = "treats";
    public static final String BACKENDLESS_TABLE_USER_COLUMN_PURCHASED_TREATS = "purchasedTreats";
    public static final String BACKENDLESS_DEFAULT_CHANNEL = "default";
    public static final String BACKENDLESS_OWNER_ID_AND_VISIBLE_WHERE_CLAUSE = AppStrings.KEY_OWNER_ID + " = " + "'" + AppStrings.BACKENDLESS_VAL_OWNER_ID + "'" + " and " + AppStrings.KEY_VISIBLE + " = true";
    public static final String BACKENDLESS_OWNER_ID__WHERE_CLAUSE = AppStrings.KEY_OWNER_ID + " = " + "'" + AppStrings.BACKENDLESS_VAL_OWNER_ID + "'";
    public static final String BACKENDLESS_SORT_CLAUSE_CREATED_DSC = "created DESC";
    public static final String BACKENDLESS_TABLE_TREAT_COLUMN_IS_VISIBLE_TO_USER = "isVisibleToUser";

    //Backendless Errors
    public static final String BACKENDLESS_ERROR_CODE_INVALID_LOGIN_OR_PASSWORD = "3003";
    public static final String BACKENDLESS_ERROR_CODE_EMPTY_PASSWORD_INPUT = "IllegalArgumentException"; //This fault code was found on debugger
    public static final String BACKENDLESS_ERROR_CODE_NO_PERMISSION_ERROR = "1011";
    public static final String BACKENDLESS_ERROR_CODE_API_CALLS_LIMIT_REACHED = "999";

    //Push Notifications
    public static final String NOTIFICATION_NEW_TREAT_CHANNEL = "treats";
    public static final String NOTIFICATION_HEADER_TICKER_TEXT = "android-ticker-text";
    public static final String NOTIFICATION_HEADER_CONTENT_TITLE = "android-content-title";
    public static final String NOTIFICATION_HEADER_CONTENT_TEXT = "android-content-text";

    //Regex
    public static final String REGEX_FIND_WHITESPACES = "\\s";
    public static final java.lang.String REGEX_ELIPSIZING_TEXT_VIEW_DEFAULT_END_PUNCTUATION = "[\\.,\u2026;\\:\\s]*$";

    //OnSavedInstanceState Keys
    public static final String SAVED_STATE_OLD_TREAT = "oldTreat";
    public static final String SAVED_STATE_OLD_TREAT_POSITION = "oldTreatPosition";

    //Prefixes
    public static String PREFIX_DRAWABLE_PATH = "android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/";

    //FCM
    public static final String VAL_SENDER_ID = "876549986257";

    //OWNER static data
    public static final String VAL_OWNER_NAME = "Winston Churchill";
    public static final String VAL_PURCHASER = "1313";

    //Data Fields
    public static final String KEY_IS_FIRST_TIME_LOGGED_IN_FOR_THIS_USER = "firstTimeLoggedIn";
    public static final String KEY_IS_PURCHASED = "isPurchased";
    public static final String KEY_IS_FIRST_LAUNCH = "isFirstLaunch";
    public static final String KEY_TREAT = "treat";
    public static final String KEY_TREAT_POSITION = "treatPosition";
    public static final String KEY_MESSAGE_FOR_DISPLAY = "messageForDisplay";
    public static final String KEY_LOGGED_IN_USER = "loggedInUser";
    public static final String KEY_USER_PURCHASES = "userPurchases";
    public static final String KEY_IMAGE = "image";

    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_OWNER_ID = "ownerId";
    public static final String KEY_NAME = "name";
    public static final String KEY_CREATED = "created";
    public static final String KEY_TEXT = "text";
    public static final String KEY_FONT_PATH = "fontPath";
    public static final String KEY_TEXT_COLOR = "textColor";
    public static final String KEY_TEXT_SIZE = "textSize";
    public static final String KEY_BG_IMAGE_NAME = "bgImageName";
    public static final String KEY_PURCHASEABLE = "purchaseable";
    public static final String KEY_VISIBLE = "visible";

    //String Helpers
    public static final String EMPTY_STRING = "";
    public static final String SPACE_STRING = " ";

    //Resources names
    public static final String KEY_BG_IMAGES = "bgImages";
    public static final String KEY_COLORS = "colors";
    public static final String KEY_FONTS = "fonts";
    public static final String KEY_FONT_SIZES = "fontSizes";

    //Drawable Backgrounds Names
    public static final String BLUE_YELLOW_BG = "blue_yellow_bg";
    public static final String PINK_BG = "pink_bg";
    public static final String PURPLE_BLUE_YELLOW_BG = "purple_blue_yellow_bg";
    public static final String PINK_GREY_BG = "pink_grey_bg";

    //Fonts Names
    public static final String MONTSERRAT_BOLD = "Mon B";
    public static final String MONTSERRAT_REG = "Mon";
    public static final String QUIRLYCUES = "Qui";
    public static final String MYRIAD_PRO_REG = "Myr";
    public static final String LATO_REG = "Lato";
    public static final String ARIAL = "Arial";
    public static final String ALEF_REG = "Alef";
    public static final String ALEF_BOLD = "Alef B";

    //Fonts Paths
    public static final String ARIAL_PATH = "fonts/Arial.ttf";
    public static final String ALEF_REG_PATH = "fonts/Alef-Regular.ttf";
    public static final String ALEF_BOLD_PATH = "fonts/Alef-Bold.ttf";
    public static final String MONTSERRAT_BOLD_PATH = "fonts/Montserrat-Bold.ttf";
    public static final String MONTSERRAT_REG_PATH = "fonts/Montserrat-Regular.ttf";
    public static final String QUIRLYCUES_PATH = "fonts/Quirlycues.ttf";
    public static final String MYRIAD_PRO_REG_PATH = "fonts/Myriad_Pro-Regular.ttf";
    public static final String LATO_REG_PATH = "fonts/Lato-Regular.ttf";

    //View Di Names
    public static final String TREAT_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA = "treatListActivityDiscreteScrollViewData";
    public static final String TREAT_DESIGNER_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA = "treatCreatorActivityDiscreteScrollViewData";
    public static final String PAGING_TREATS_LIST_PROGRESS_DIALOG = "treatsListPagingProgressDialog";
    public static final String MAIN_PROGRESS_DIALOG = "treatsListMainProgressDialog";
    public static final String LOGIN_LOGOUT_PROGRESS_DIALOG = "treatsListLoginLogoutProgressDialog";

    //Backendless Query builders names
    public static final String ALL_OWNER_TREATS_QUERY_BUILDER = "pageTreatsQueryBuilder";
    public static final String PURCHASED_TREATS_QUERY_BUILDER = "purchasedTreatsQueryBuilder";

}



