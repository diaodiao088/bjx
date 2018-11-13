package com.bjxapp.worker.global;

public class Constant {

	//package name
	public static final String APP_PACKAGE_NAME = "com.bjxapp.worker";
	
	//logger
	public static final String APP_LOG_TAG = "xapp";
	
	//need update app filename
	public static final String APP_UPDATE_FILENAME = "XApp.apk";
	
	//disk cache dir
	public static final String SDCARD_CACHE_DIR = ".xapp/";
	public static final String IMAGE_CACHE_FILE_EXT = ".xm";
	
	//user table
	public static final String COL_USER_ID = "id";
	public static final String COL_USER_NAME = "name";
	public static final String COL_USER_PASSWORD = "password";
	
	//receiver define
	public static final String EXTRA_KEY_RECEIVER_ID = "extra_key_receiver_id";
	public static final int RECEIVER_ID_VALUE_ERROR = -1;
	public static final int RECEIVER_ID_VALUE_NETWORK_STATE_CHANGED = 1;
	
	//notify define
	public static final String EXTRA_KEY_ENTER_IN_APP_METHOD = "extra_key_enter_in_app_method";
	public static final String EXTRA_KEY_CLASS_NAME = "class_name";
	public static final String EXTRA_RETURN_KEY_CLASS_NAME = "return_class_name";
	public static final int EXTRA_VALUE_ENTER_IN_APP_FROM_ICON = 0;
	public static final int EXTRA_VALUE_ENTER_IN_APP_FROM_NOTIFY = 1;
	public static final int EXTRA_VALUE_ENTER_IN_APP_FROM_PUSH = 3;
	public static final int EXTRA_VALUE_ENTER_IN_APP_FROM_OTHER = 4;
    public static final int EXTRA_VALUE_ENTER_IN_APP_FROM_WAP = 6;
    
    //notify id define
    public static final int NOTIFY_ID_UPDATE_APP = 1000;
	public static final int NOTIFY_ID_COMMON = 1001;
	public static final int NOTIFY_ID_PUSH = 1002;
	public static final int NOTIFY_ID_SELF_DEFINE = 1003;
	
	//clip intent key define
	public static final String EXTRA_KEY_USER_BITMAP = "user_bitmap";
    public static final String EXTRA_KEY_USER_BITMAP_URI = "user_bitmap_uri";
	public static final String EXTRA_KEY_CLIP_WHAT = "user_bitmap_clip_what";
	public static final String EXTRA_KEY_CLIP_VIEW_NEED_CUT = "extra_key_clip_view_need_cut";
	
	//modal activity request code define
	public static final int REQUEST_CODE_CLOCK_CHOOSE_LOCAL_IMG = 17;
	public static final int REQUEST_CODE_CLOCK_TAKE_PHOTO = 18;
	public static final int REQUEST_CODE_CLOCK_CLIP_IMG = 19;
	
	//main activity background
	public static final String FILE_NAME_MAIN_ACTIVITY_BACKGROUND = "main_activity_background";
	
	//upload & download url dir type
	public static final long UPLOAD_IMAGE_SIZE = 1024 * 300;
	public static final String UPLOAD_IMAGE_EXT = ".jpg";
	public static final String UPLOAD_URL_SERVER_DIR_DEFAULT = "uploaded/default";
	public static final String UPLOAD_URL_SERVER_DIR_USER = "uploaded";
	public static final String UPLOAD_URL_SERVER_DIR_USER_ID = "uploaded";
	
	//locate main activity index
	public static final String LOCATE_MAIN_ACTIVITY_INDEX = "locate_main_activity_index";
	
	//push define
	public static final int PUSH_TYPE_NORMAL = 0;
	public static final int PUSH_TYPE_WEB_ACTIVITY = 1;
	public static final int PUSH_TYPE_MESSAGE_NEW = 9;
	
	//订单状态推送，10：新订单,11：已支付,12：已评价,13：已取消,14：异常
	public static final int PUSH_TYPE_ORDER_NEW = 10;
	public static final int PUSH_TYPE_ORDER_PAY = 11;
	public static final int PUSH_TYPE_ORDER_SCORE = 12;
	public static final int PUSH_TYPE_ORDER_CANCEL = 13;
	public static final int PUSH_TYPE_ORDER_EXCEPTION = 14;
	
	public static final String PUSH_ACTION_ORDER_MODIFIED = "com.bjxapp.worker.action.ORDER_MODIFIED";
	public static final String PUSH_ACTION_MESSAGE_MODIFIED = "com.bjxapp.worker.action.MESSAGE_MODIFIED";

	public static final String ACTION_USER_EXPIRED = "action_user_expired";
	
	//consult type define
	public static final int CONSULT_TYPE_YEARS = 100;
	public static final int CONSULT_WORK_SORTS = 101;
	public static final int CONSULT_WORK_MAP = 102;
	public static final int CONSULT_ID_IMAGES = 103;
	public static final int CONSULT_WORK_CITY = 104;
	public static final int CONSULT_SETTING_PWD = 105;
	
	//activity type define
	public static final int ACTIVITY_APPLY_RESULT_CODE = 201;
	public static final int ACTIVITY_ORDER_ADDITION_RESULT_CODE = 202;
	public static final int ACTIVITY_ORDER_DETAIL_RESULT_CODE = 203;
	public static final int ACTIVITY_WITHDRAW_RESULT_CODE = 204;
	
	//edit dialog type
	public static final int EDIT_DIALOG_TYPE_NORMAL = 0;
	public static final int EDIT_DIALOG_TYPE_REMARK = 1;
	public static final int EDIT_DIALOG_TYPE_NUMBER = 2;
	
	//bill operation type
	public static final int BILL_OPERATION_TYPE_NEW = 0;
	public static final int BILL_OPERATION_TYPE_UPDATE = 1;
	public static final int BILL_OPERATION_TYPE_BWORSE = 2;
	
	//user location
	public static double USER_LOCATION_LATITUDE = 0.0;
	public static double USER_LOCATION_LONGITUDE = 0.0;
	public static String USER_LOCATION_ADDRESS = "";
	public static String USER_LOCATION_CITY = "";
	
	//upload image type
	public static final int UPLOAD_IMAGE_ID = 1;
	public static final int UPLOAD_IMAGE_ORDER = 2;
	
}