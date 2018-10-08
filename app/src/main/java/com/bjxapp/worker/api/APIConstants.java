package com.bjxapp.worker.api;

public class APIConstants {
	// Params
    public static final String URL_SID_PARAM = "sid";
    public static final String URL_UUID_PARAM = "uuid";
    public static final String URL_PM_PARAM = "pm";
    public static final String URL_SYS_PARAM = "sys";
    public static final String URL_APP_VER_PARAM = "app_ver";
    public static final String URL_CHANNEL_PARAM = "channel";
    public static final String URL_LANGUAGE_PARAM = "language";
    public static final String URL_PLATFORM_PARAM = "platform";
    public static final String URL_USER_ID_PARAM = "user_id";
    public static final String URL_LAST_MODIFIED_PARAM = "last_modified";
    public static final String URL_SCREEN_WIDTH = "width";
    public static final String URL_SCREEN_HEIGHT = "height";
    
    // Result
    public static final String JSON_RESULT_CODE_KEY = "result_code";
    public static final int RESULT_CODE_SUCCESS = 200;
    public static final int RESULT_CODE_NOT_MODIFIED = 304;
    
    //139.129.208.245
	// account api
    public static final String ACCOUNT_SENDAUTH_API = "http://www.bjxapp.com:82/account/authcode";
	public static final String ACCOUNT_LOGIN_API = "http://www.bjxapp.com:82/account/login";
	public static final String ACCOUNT_SERVICE_SUB_GET_API = "http://www.bjxapp.com:82/account/service/get";
	public static final String ACCOUNT_STATUS_API = "http://www.bjxapp.com:82/account/status";
	public static final String ACCOUNT_REGISTER_SAVE_API = "http://www.bjxapp.com:82/account/save";
	public static final String ACCOUNT_REGISTER_GET_API = "http://www.bjxapp.com:82/account/get";
	public static final String ACCOUNT_CHANNEL_API = "http://www.bjxapp.com:82/account/channel";
	public static final String ACCOUNT_MESSAGE_LIST_API = "http://www.bjxapp.com:82/account/message/list";
	public static final String ACCOUNT_MESSAGE_DETAIL_API = "http://www.bjxapp.com:82/account/message/detail";
    public static final String UPDATE_INFO_API = "http://www.bjxapp.com:82/account/update/get";
    
    //image validate code
    public static final String ACCOUNT_KEY_GET_API = "http://www.bjxapp.com:82/account/login/key";
    public static final String ACCOUNT_VERIFY_CODE_GET_API = "http://www.bjxapp.com:82/account/verifycode";
    
    // account balance api
    public static final String ACCOUNT_BALANCE_BANK_EXISTS = "http://www.bjxapp.com:82/account/bank/exists";
    public static final String ACCOUNT_BALANCE_BANK_SAVE = "http://www.bjxapp.com:82/account/bank";
    public static final String ACCOUNT_BALANCE_BANK_GET = "http://www.bjxapp.com:82/account/bank/get";
    public static final String ACCOUNT_BALANCE_WITHDRAW_ALLOW = "http://www.bjxapp.com:82/account/cash/allow";
    public static final String ACCOUNT_BALANCE_WITHDRAW = "http://www.bjxapp.com:82/account/cash";
    public static final String ACCOUNT_BALANCE_WITHDRAW_LIST = "http://www.bjxapp.com:82/account/cash/list";
    
    //desktop order receive
    public static final String DESKTOP_INDEX_GET = "http://www.bjxapp.com:82/desktop/index/get";
    public static final String DESKTOP_ORDER_RECEIVE_SETTING = "http://www.bjxapp.com:82/desktop/index/sign";
    public static final String DESKTOP_DOT_GET_URL = "http://www.bjxapp.com:82/desktop/dot/get";
    public static final String DESKTOP_ORDER_LIST_GET = "http://www.bjxapp.com:82/desktop/order/list";
    public static final String DESKTOP_ORDER_DETAIL_GET = "http://www.bjxapp.com:82/desktop/orderdetail/get";
    public static final String DESKTOP_ORDER_RECEIVE_SAVE = "http://www.bjxapp.com:82/desktop/order/receive";
    public static final String DESKTOP_ORDER_ADDITION_SAVE = "http://www.bjxapp.com:82/desktop/order/add";
    public static final String DESKTOP_ORDER_FINISH_SAVE = "http://www.bjxapp.com:82/desktop/order/finish";
    public static final String DESKTOP_ORDER_PAY_URL_GET = "http://www.bjxapp.com:82/desktop/pay/url";
    
    //image upload and download url
    public static final String IMAGE_HEAD_UPLOAD_URL = "http://www.bjxapp.com:82/upload/head";
    public static final String IMAGE_HEAD_DOWNLOAD_URL = "http://www.bjxapp.com:82/upload/head";
    public static final String IMAGE_IDS_UPLOAD_URL = "http://www.bjxapp.com:82/upload/card";
  
}
