package com.notifii.notifiiapp.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.notifii.notifiiapp.BuildConfig;

public interface NTF_Constants {

    int ONE_SECOND = 1000;
    int ONE_MINUTE = 60 * ONE_SECOND;
    String APP_HEADER = "User-Agent";
    String GLOBAL_DATA_FILE_NAME = "data.json";
    String SDCARD_PATH = String.valueOf(Environment.getExternalStorageDirectory());
    String RECIPIENT_DATA_FILE_NAME = "recipients_data.json";
    String DISPLAY_DATE_FORMATE = "MM/dd/yyyy";
    String DATE_TIME_FORMATE = "yyyy-MM-dd HH:mm:ss";
    String KEYBOARD_BROADCAST_RECEIVER_ACTION = "keyboard_broadcast_receiver_action";
    String IS_KEBOARD_OPEN = "is_keyboard_open";
    int CLICK_PHOTO_REQUEST_CODE_1 = 4;
    int CLICK_PHOTO_REQUEST_CODE_2 = 5;
    int CLICK_PHOTO_REQUEST_CODE_3 = 6;
    int OCR_REQUEST_CODE = 100;
    int PIC_WIDTH = 600;
    long SCREEN_AUTO_REFRESH_INTERVAL = 60 * ONE_MINUTE;
//    long SCREEN_AUTO_REFRESH_INTERVAL = 5 * ONE_MINUTE;
    String LPOFTABCLICKED = "tab_2_clicked";
    String LPOFTABCLICKEDFROM = "tab_2_clicked_FROM";

    String ALERT_WARNING_TITLE = "Uh oh!";
    String ALERT_SUCCESS_TITLE = "Success!";
    String ALERT_ERROR_TITLE_OOPS = "Oops!";
    String ALERT_SUCCESS_TITLE_AWESOME_JOB = "Awesome job!";

    class Base_API {
        public static final String BASE_URL = BuildConfig.BASE_URL;
    }

    class Service_URLs {
        public static final String LOGIN_URL = "app-login.php";
        public static final String GLOBAL_CONSTANTS = "get-global-constants.php";
        public static final String RECIPIENT_LIST_URL = "recipient-list.php";
        public static final String SEARCH_RECIPIENT_URL = "search-recipients.php";
        public static final String PACKAGE_LOGIN_URL = "package-login.php";
        public static final String PACKAGE_LOOKUP_URL = "package-lookup.php";
        public static final String PACKAGES_FOR_RECIPIENT_URL = "packages-for-recipient.php";
        public static final String GET_RECIPIENT_URL = "get-recipient.php";
        public static final String UPDATE_RECIPIENT_URL = "update-recipient.php";
        public static final String ADD_RECIPIENT_URL = "add-recipient.php";
        public static final String GET_PACKAGE_DETAILS_URL = "get-package.php";
        public static final String PACKAGES_PENDING_WITH_IMAGES_URL = "packages-pending-with-images.php";
        public static final String UPDATE_PACKAGE_URL = "update-package.php";
        public static final String MULTIPLE_UPDATE_PACKAGE_URL = "edit-package.php";
        public static final String App_LOGOUT_URL = "app-logout.php";
        public static final String UPDATE_PASSWORD_URL = "password-update.php";
        public static final String UPDATE_USERNAME_URL = "username-update.php";
        public static final String CONTACT_US_URL = "contact-us.php";
        public static final String PACKAGE_HISTORY_URL = "package-history-for-account.php";
        public static final String FORGOT_PASSWORD = "forgot-password.php";
        public static final String USERNAME_PASSWORD_UPDATE = "username-password-update.php";
        public static final String USERNAME_AVAILABILITY = "username-availability.php";
        public static final String PROFILE_URL = "get-user-details.php";
        public static final String UPDATE_NAME_EMAIL = "update-user-details.php";
        public static final String SEARCH_PACKAGE_URL = "search-packages.php";
        public static final String RECIPIENT_PENDING_PACKAGES = "recipient-pending-packages.php";
        public static final String LOGOUT_SIGNATURE_KIOSK_DATA = "package-logout-data.php";
        public static final String LOGOUT_SIGNATURE_KIOSK_IMAGES = "package-logout-images.php";
        public static final String KIOSK_LOGIN = "kiosk-login.php";
        public static final String KIOSK_LOGOUT_URL = "kiosk-logout.php";
        public static final String SSO_LOGIN_REQUEST = "sso-login-request.php";
        public static final String SSO_LOGIN_RESPONSE = "sso-login-response.php";
        public static final String KIOSK_SSO_LOGOUT_REQUEST = "kiosk-sso-logout.php";
        public static final String MFA_LOGIN = "login-mfa.php";
        public static final String RESEND_MFA = "resend-mfa-code.php";
        public static final String MULTISITE_LOGIN = "app-login-multisite.php";
        public static final String SWITCH_ACCOUNTS = "app-switch-accounts.php";
    }

    class ResponseKeys {
        public static final String SUCCESS = "success";
        public static final String ERROR = "error";
        public static final String SESSION_EXPIRED = "session_expired";
        public static final String WARNING = "warning";
        public static final String ACCOUNT_CLOSED = "account_closed";
        public static final String ACCOUNT_SUSPENDED = "account_suspended";
        public static final String RESET_PASSWORD = "reset_password";
        public static final String RESET_USERNAME_PASSWORD = "reset_username_password";
        public static final String TAKEN = "taken";
        public static final String AVAILABLE = "available";
        public static final String FOUND = "found";
        public static final String NOT_FOUND = "not_found";
        public static final String SELECT_ACCOUNT = "select_account";
        public static final String MFA_API_STATUS = "loggedin_with_mfa_code";
        public static final String DISABLE_RESEND = "disable_resend_button";
        public static final String MULTISITE_USER = "multisite_user";
    }

    class Extras_Keys {
        public static final String REFRESH_LOG_PACKAGE_OUT_LIST = "refresh_log_package_out_list";
        public static Bitmap IMAGE = null;
        public static final String LOGIN_API_STATUS = "loginApiStatus";
        public static final String KEY_PHOT_POS = "photoPosition";
        public static final String KEY_RECIPIENT = "keyRecepient";
        public static final String KEY_RECIPIENT_ID = "recipientId";
        public static final String KEY_PKG_ID = "pkgId";
        public static final String KEY_PACKAGE_LIST = "pakgsList";
        public static final String PKG_HISTORY_RESIDENTS_FRAGMENTS = "pkgHistoryResidentsFragment";
        public static final String PKG_HISTORY_UNIT_FRAGMENTS = "pkgHistoryUnitFragment";
        public static final String FIELDS_REFRESHED = "fieldsRefreshed";
        public static final String IS_FROM_SEARCH_PACKAGE = "isfrom_searchPackage";
        public static final String IS_FROM_SEARCH_RESIDENT = "is_from_search_resident";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "last_name";
        public static final String UNIT_NUMBER = "unit_no";
        public static final String EMAIL = "email";
        public static final String CELLPHONE = "cellphone";
        public static final String SEARCH_LOGIC = "search_logic";
        public static final String DATE_RANGE = "date_range";
        public static final String STATUS_FILTER = "status_filter";
        public static final String TRACKING_NUMBER = "tracking_no";
        public static final String RECIPIENT_NAME = "recipient_name";
        public static final String MAILBOX_NUMBER = "mailbox_number";
        public static final String SENDER = "sender";
        public static final String SHELF = "shelf";
        public static final String TAG_NUMBER = "tag_number";
        public static final String CUSTOM_MESSAGE = "custom_message";
        public static final String STAFF_NOTES = "staff_notes";
        public static final String SHOW_ADVANCED_OPTIONS = "showAdvOptions";
        public static final String PO_NUMBER = "po_number";
        public static final String DETECTED_TEXT = "detected_text";
        public static String OTHERS = "Other";
        public static final String ALGORITHM_TIME = "algorithm_time";
        public static final String OCR_TIME = "ocr_time";
        public static String LOCAL_BROADCAST_START_ACTION = "local_broadcast_start_action";
        public static String LOCAL_BROADCAST_END_ACTION = "local_broadcast_end_action";
        public static String BACKGROUNG_API_STATUS = "backgroung_api_status";
        public static String BACKGROUNG_API_MESSAGE = "background_api_message";
    }

    class Prefs_Keys {
        public static final String LOGIN_API_STATUS = "loginAPIStatus";
        public static final String LOGIN_SSO_EMAIL = "login_sso_emails";
        public static final String SESSION_ID = "sessionId";
        public static final String AUTHENTICATION_TOKEN = "authenticationToken";
        public static final String EXPIRATION = "expiration";
        public static final String USER_ID = "userId";
        public static final String FULL_NAME = "fullName";
        public static final String ACCOUNT_ID = "accountId";
        public static final String ACCOUNT_NAME = "accountName";
        public static final String ACCOUNT_TYPE = "accountType";
        public static final String USER_TYPE = "userType";
        public static final String TIMEZONE = "timezone";
        public static final String KIOSK_MODE_DISCLAIMER = "kiosk_mode_disclaimer";
        public static final String KIOSK_DISPLAY_RECIPIENT_FORMAT = "kiosk_display_recipient_format";
        public static final String DEFAULT_MAILROOM_ID = "defaultMailRoomId";
        public static final String IS_LOGGED_IN = "isLoggedIn";
        public static final String IS_KIOSK_MODE = "is_kiosk_mode";
        public static final String OS_VERSION = "deviceOSVersion";
        public static final String PKG_LOGIN_CARRIER = "pkg_login_carrier";
        public static final String PKG_LOGIN_SENDER = "pkg_login_sender";
        public static final String PKG_LOGIN_SERVICETYPE = "pkg_login_servicetype";
        public static final String PKG_LOGIN_PACKAGETYPE = "pkg_login_packagetype";
        public static final String PKG_LOGIN_CONDITION = "pkg_login_condition";
        public static final String PKG_LOGIN_SHELF = "pkg_login_shelf";
        public static final String PKG_LOGOUT_SHELF = "pkg_logout_shelf";
        public static final String PKG_LOGOUT_ADDRESS1 = "pkg_logout_address1";
        public static final String PKG_LOGOUT_PACKAGETYPE = "pkg_logout_packagetype";
        public static final String PKG_LOGOUT_MAILROOM = "pkg_logout_mailroom";
        public static final String PKG_LOGIN_PONUMBER = "pkg_login_ponumber";
        public static final String PKG_LOGIN_DATA_INTEGRATION = "pkg_login_data_integration";
        public static final String PKG_LOGOUT_SENDER = "pkg_logout_sender";
        public static final String PKG_LOGOUT_TAGNUMBER = "pkg_logout_tagnumber";
        public static final String RECIPIENT_PKG_LIST_SELECTED_ITEM2 = "recipient_pkg_list_selected_item";
        public static final String RECIPIENT_ADDED_OR_UPDATED = "recipient_added_or_updated";
        public static final String INFO_ORIENTATION = "infoOrientation";
        public static final String EMAIL = "pm_email";
        public static final String USER_NAME = "userName";
        public static final String IS_FIELDS_REFRESHED = "isFieldsRefreshed";
        public static final String PKG_LOGIN_SPECIAL_HANDLING = "pkg_login_special_handling";
        public static final String PKG_LOGIN_CUSTOM_MESSAGE = "pkg_login_custom_message";
        public static final String PKG_LOGIN_PICTURES = "pkg_login_pictures";
        public static final String PRIVILEGE_ADMIN_ACCOUNT = "privilege_admin_account";
        public static final String PRIVILEGE_ADMIN_USERS = "previlege_admin_users";
        public static final String PRIVILEGE_CORE_RECIPIENTS = "previlege_core_recipients";
        public static final String PRIVILEGE_TRACK_PACKAGES = "previlege_track_packages";
        public static final String USER_TYPE_ID = "user_type_id";
        public static final String UNIT_NUMBER_ORDER = "unit_number_order";
        public static final String RECIPIENT_NAME_ORDER = "recipientNameOrder";
        public static final String TRACKING_NUMBER_ORDER = "tracking_number_order";
        public static final String TAG_NUMBER_ORDER = "tag_number";
        public static final String PACKAGE_TYPE_ORDER = "package_type_order";
        public static final String SHELF_ORDER = "shelf_order";
        public static final String SENDER_ORDER = "sender_order";
        public static final String DATE_RECIEVED_ORDER = "date_recieved_order";
        public static final String MAIL_ROOM_ORDER = "mail_room";
        public static final String SEND_PKG_LOGIN_NOTIFICATION = "sendPkgLoginNotification";
        public static final String SEND_PKG_LOGOUT_NOTIFICATION = "sendPkgLogoutNotification";
        public static final String IS_ALARM_RUNNING = "alarmRunning";
        public static final String SYNC_FAILED_DUE_TO_CONNECTIVITY = "syncFailed";
        public static final String HAS_LOGIN = "has_login";
        public static final String OCR_TIME_LIMIT = "ocr_time_limit";
        public static final String REQUIRE_EXPLICIT_SMS_OPT_IN = "require_explicit_sms_opt_in";
        public static final String PKG_LOGIN_WEIGHT = "pkg_login_weight";
        public static final String PKG_LOGIN_DIMENSIONS = "pkg_login_dimensions";
        public static final String TIMEZONE_SHORTCODE = "timezoneidShortcode";
        public static final String SESSION_TIMEOUT = "session_timeout";
        public static final String SELECTED_MAILROOM = "selected_mailrooms";
        public static final String SSO_LOGIN_OPTION  = "sso_login_option";
        public static final String SSO_STATUS = "sso_status";
        public static final String EMAIL_OPTION_ENABLED = "email_option_enabled";
        public static final String USE_FRONT_CAMERA = "use_front_camera";
        public static final String PENDING_PACKAGES_PRIMARY_SORT_COLUMN = "pending_packages_primary_sort_column";
        public static final String PENDING_PACKAGES_PRIMARY_SORT_ORDER = "pending_packages_primary_sort_order";
        public static final String PENDING_PACKAGES_SECONDARY_SORT_COLUMN = "pending_packages_secondary_sort_column";
        public static final String PENDING_PACKAGES_SECONDARY_SORT_ORDER = "pending_packages_secondary_sort_order";
    }

}

