package inspire.ariel.inspire.common.constants;

import inspire.ariel.inspire.BuildConfig;

public class AppStrings {

    /**
    * Static constants class which contains all strings that are NOT directly presented
    * to the user (excluding log messages).
    * All user presented strings are located on R.string in the res package
    */

    //Backendless
    public static final String BACKENDLESS_APPLICATION_ID = "1C0DA01D-01F8-CC77-FF89-629A70D7CB00";
    public static final String BACKENDLESS_API_KEY = "D5242878-29DD-0377-FF39-FC1E91E64300";
    public static final String BACKENDLESS_SERVER_URL = "https://api.backendless.com";
    public static final String BACKENDLESS_TABLE_QUOTE = "Quote";
    public static final String BACKENDLESS_TABLE_LEADER = "Leader";
    public static final String BACKENDLESS_TABLE_LEADER_COLUMN_QUOTES = "quotes";
    public static final String BACKENDLESS_CHANNEL_DEFAULT = "default";

    //Background names
    public static final String BLUE_YELLOW_BG = "blue_yellow_bg";
    public static final String PINK_BG = "pink_bg";
    public static final String PURPLE_BLUE_YELLOW_BG = "purple_blue_yellow_bg";
    public static final String PINK_GREY_BG = "pink_grey_bg";

    //Animations names
    public static final String FADE_IN_ANIM = "fadeIn";
    public static final String FADE_OUT_ANIM = "fadeOut";

    //Regex
    public static final String FIND_WHITESPACES_REGEX = "\\s";

    //Prefixes
    public static String DRAWABLE_PATH_PREFIX = "android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/";

    //FCM
    public static final String SENDER_ID = "876549986257";

    //Data Fields
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String EMAIL = "email";
    public static final String IS_FIRST_LAUNCH = "isFirstLaunch";
    public static final String PROFILE_IMAGE_URL = "profileImageUrl";

    //Device Data Fields
    public static final String DEVICE_STRING_HEIGHT = "deviceHeight";
    public static final String DEVICE_STRING_WIDTH = "deviceWidth";

    //Other
    public static final String LEADER_NAME = "Winston Churchill";
    public static final String LEADER_DEVICE_ID = "B8E5E40A-4575-3599-FF01-A18C28199B00";
    public static final String EMPTY_STRING = "";
    public static final String ACCOUNT_INFO = "Account Info";

    //Resources names
    public static final String BG_IMAGES = "bgImages";
    public static final String COLORS = "colors";
    public static final String FONTS = "fonts";
    public static final String FONT_SIZES = "fontSizes";

    //Fonts Names
    public static final String MONTSERRAT_BOLD = "Mon B";
    public static final String MONTSERRAT_REG = "Mon";
    public static final String QUIRLYCUES = "Qui";
    public static final String MYRIAD_PRO_REG = "Myr";
    public static final String LARO_REG = "Lat";
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

}

