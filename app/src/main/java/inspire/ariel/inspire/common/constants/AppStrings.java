package inspire.ariel.inspire.common.constants;

import inspire.ariel.inspire.BuildConfig;

public class AppStrings {

    /**
    * Static constants class which contains all strings that are NOT directly presented
    * to the user.
    * All user presented strings are located on R.string in the res package
    */

    //Backendless
    public static final String BACKENDLESS_VAL_APPLICATION_ID = "1C0DA01D-01F8-CC77-FF89-629A70D7CB00";
    public static final String BACKENDLESS_VAL_API_KEY = "D5242878-29DD-0377-FF39-FC1E91E64300";
    public static final String BACKENDLESS_VAL_OWNER_DEVICE_ID = "LGK4205a6e9a63";
    public static final String BACKENDLESS_TABLE_TREATS = "Treat";
    public static final String BACKENDLESS_TABLE_USERS = "Users";
    public static final String BACKENDLESS_TABLE_USER_COLUMN_TREATS = "treats";
    public static final String BACKENDLESS_DEFAULT_CHANNEL = "default";
    public static final String BACKENDLESS_OWNER_ID_WHERE_CLAUSE = "ownerId = " + "'" + AppStrings.VAL_OWNER_OBJECT_ID + "'";
    public static final String BACKENDLESS_SORT_CLAUSE_CREATED_DSC = "created DESC";

    //Backendless Errors
    public static final String BACKENDLESS_ERROR_CODE_INVALID_LOGIN_OR_PASSWORD = "3003";
    public static final String BACKENDLESS_ERROR_CODE_EMPTY_PASSWORD_INPUT = "IllegalArgumentException"; //This fault code was found on debugger
    public static final String BACKENDLESS_ERROR_CODE_NO_PERMISSION_ERROR = "1011";

    //Push Notifications
    public static final String NOTIFICATION_NEW_TREAT_CHANNEL = "treats";
    public static final String NOTIFICATION_HEADER_TICKER_TEXT = "android-ticker-text";
    public static final String NOTIFICATION_HEADER_CONTENT_TITLE = "android-content-title";
    public static final String NOTIFICATION_HEADER_CONTENT_TEXT = "android-content-text";

    //Regex
    public static final String REGEX_FIND_WHITESPACES = "\\s";
    public static final java.lang.String REGEX_ELIPSIZING_TEXT_VIEW_DEFAULT_END_PUNCTUATION = "[\\.,\u2026;\\:\\s]*$";

    //Prefixes
    public static String PREFIX_DRAWABLE_PATH = "android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/";

    //FCM
    public static final String VAL_SENDER_ID = "876549986257";

    //OWNER static data
    public static final String VAL_OWNER_NAME = "Winston Churchill";
    public static String VAL_OWNER_DESCRIPTION = "My name is Winston Churchill";
    public static final String VAL_OWNER_OBJECT_ID = "B2FD1088-D093-1C3E-FFFE-341893C28000";

    //Data Fields
    public static final String KEY_IS_USER_OWNER = "isUserOwner";
    public static final String IS_FIRST_LAUNCH = "isFirstLaunch";

    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_OWNER_ID = "ownerId";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_TEXT = "text";
    public static final String KEY_FONT_PATH = "fontPath";
    public static final String KEY_TEXT_COLOR = "textColor";
    public static final String KEY_TEXT_SIZE = "textSize";
    public static final String KEY_BG_IMAGE_NAME = "bgImageName";
    public static final String KEY_TREAT = "treat";
    public static final String KEY_TREAT_POSITION = "treatPosition";

    //Device Data Fields
    public static final String KEY_DEVICE_STRING_HEIGHT = "deviceHeight";
    public static final String KEY_DEVICE_STRING_WIDTH = "deviceWidth";

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
    public static final String TREAT_CREATOR_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA = "treatCreatorActivityDiscreteScrollViewData";
    public static final String PAGING_TREATS_LIST_PROGRESS_DIALOG = "treatsListPagingProgressDialog";
    public static final String MAIN_PROGRESS_DIALOG = "treatsListMainProgressDialog";
    public static final String LOGIN_LOGOUT_PROGRESS_DIALOG = "treatsListLoginLogoutProgressDialog";


    //Other
    public static final String ACCOUNT_INFO = "Account Info";
}



