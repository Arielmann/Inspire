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
    public static final String BACKENDLESS_TABLE_QUOTE = "Quote";
    public static final String BACKENDLESS_TABLE_LEADER = "Leader";
    public static final String BACKENDLESS_TABLE_LEADER_COLUMN_QUOTES = "quotes";
    public static final String BACKENDLESS_DEFAULT_CHANNEL = "default";
    public static final String BACKENDLESS_LEADER_ID_WHERE_CLAUSE = "leaderId = " + "'" + AppStrings.VAL_LEADER_OBJECT_ID + "'";
    public static final String BACKENDLESS_SORT_CLAUSE_CREATED_DSC = "created DESC";

    //Push Notifications
    public static final String NOTIFICATION_NEW_QUOTE_CHANNEL = "quotes";
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

    //Date Formats
    public static final java.lang.String SIMPLE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    //Leader static data
    public static final String VAL_LEADER_NAME = "Winston Churchill";
    public static String VAL_LEADER_DESCRIPTION = "My name is Winston Churchill";
    public static final String VAL_LEADER_OBJECT_ID = "B8E5E40A-4575-3599-FF01-A18C28199B00";

    //Data Fields
    public static final String KEY_OBJECT_ID = "objectId";
    public static final String KEY_LEADER_ID = "leaderId";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IS_FIRST_LAUNCH = "isFirstLaunch";
    public static final String KEY_TEXT = "text";
    public static final String KEY_FONT_PATH = "fontPath";
    public static final String KEY_TEXT_COLOR = "textColor";
    public static final String KEY_TEXT_SIZE = "textSize";
    public static final String KEY_BG_IMAGE_NAME = "bgImageName";
    public static final String KEY_QUOTE = "quote";

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
    public static final String QUOTE_LIST_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA = "quoteListActivityDiscreteScrollViewData";
    public static final String QUOTE_CREATOR_ACTIVITY_DISCRETE_SCROLL_VIEW_DATA = "quoteCreatorActivityDiscreteScrollViewData";

    //Other
    public static final String ACCOUNT_INFO = "Account Info";
}



