package com.bjxapp.worker.global;

import com.bjxapp.worker.ui.view.activity.widget.treeview.SimpleTreeRecyclerAdapter;
import com.bjxapp.worker.utils.Env;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class ConfigManager {

	//user info
	private static final String PREF_KEY_USER_CODE = "pref_key_user_code";
    private static final String PREF_KEY_USER_NAME = "pref_key_user_name";
    private static final String PREF_KEY_USER_STATUS = "pref_key_user_status";
    private static final String PREF_KEY_USER_SESSION = "pref_key_user_session";
    private static final String PREF_KEY_USER_HEAD_IMAGE_URL = "pref_key_user_head_image_url";
    
    //push channelid
    private static final String PREF_KEY_USER_CHANNELID = "pref_key_user_channelid";
    private static final String PREF_KEY_USER_CHANNELID_UPLOADED = "pred_key_user_channelid_uploaded";
    
    //app update info
    private static final String PREF_KEY_NEED_UPDATE = "pref_key_need_update";
    private static final String PREF_KEY_UPDATE_URL = "pref_key_update_url";
    private static final String PREF_KEY_UPDATE_DESCRIPTION = "pref_key_update_description";
    private static final String PREF_KEY_UPDATE_VERSION = "pref_key_update_version";
    
    //desktop red dot 
    private static final String PREF_KEY_DESKTOP_MESSAGES_DOT = "pref_key_desktop_messages_dot";
    private static final String PREF_KEY_DESKTOP_MESSAGES_DOT_SERVER = "pref_key_desktop_messages_dot_server";
    private static final String PREF_KEY_DESKTOP_ORDERS_DOT = "pref_key_desktop_orders_dot";
    private static final String PREF_KEY_DESKTOP_ORDERS_DOT_SERVER = "pref_key_desktop_orders_dot_server";

    private static final String PREF_KEY_RECEIVER_BILL = "pref_key_receiver_bill";

    private static ConfigManager instance;
    private Context mContext;
    private SharedPreferences sp;

    @SuppressLint("InlinedApi")
    private ConfigManager(Context context) {
        mContext = context;
        if (Env.getSDKLevel() < Env.ANDROID_3_0) {
            sp = context.getSharedPreferences(
                    getDefaultSharedPreferencesName(context),
                    Context.MODE_PRIVATE);
        } else {
            sp = context.getSharedPreferences(
                    getDefaultSharedPreferencesName(context),
                    Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        }
    }

    public synchronized static ConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigManager(context.getApplicationContext());
        }
        return instance;
    }
    
    protected Context getContext() {
        return mContext;
    }
    
    protected String getResourceString(int iResource) {
        return mContext.getString(iResource);
    }

    private String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    public void setUserCode(String userCode){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_USER_CODE, userCode);
    	e.commit();
    }
    
    public String getUserCode(){
    	return sp.getString(PREF_KEY_USER_CODE, "");
    }
    
    public void setUserName(String userName){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_USER_NAME, userName);
    	e.commit();
    }
    
    public String getUserName(){
    	return sp.getString(PREF_KEY_USER_NAME, "");
    }
    
    public void setUserStatus(int status){
    	Editor e = sp.edit();
    	e.putInt(PREF_KEY_USER_STATUS, status);
    	e.commit();
    }
    
    public int getUserStatus(){
    	return sp.getInt(PREF_KEY_USER_STATUS, -1);
    }
    
    public void setUserSession(String userSession){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_USER_SESSION, userSession);
    	e.commit();
    }
    
    public String getUserSession(){
    	return sp.getString(PREF_KEY_USER_SESSION, "");
    }
    
    public void setUserHeadImageUrl(String userHeadImageUrl){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_USER_HEAD_IMAGE_URL, userHeadImageUrl);
    	e.commit();
    }
    
    public String getUserHeadImageUrl(){
    	return sp.getString(PREF_KEY_USER_HEAD_IMAGE_URL, "");
    }
    
    public void setUserChannelID(String channelID){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_USER_CHANNELID, channelID);
    	e.commit();
    }
    
    public String getUserChannelID(){
    	return sp.getString(PREF_KEY_USER_CHANNELID, "");
    }
    
    public void setUserChannelUploaded(Boolean uploaded){
    	Editor e = sp.edit();
    	e.putBoolean(PREF_KEY_USER_CHANNELID_UPLOADED, uploaded);
    	e.commit();
    }
    
    public Boolean getUserChannelUploaded(){
    	return sp.getBoolean(PREF_KEY_USER_CHANNELID_UPLOADED, false);
    }
    
    public void setNeedUpdate(Boolean needUpdate){
    	Editor e = sp.edit();
    	e.putBoolean(PREF_KEY_NEED_UPDATE, needUpdate);
    	e.commit();
    }
    
    public Boolean getNeedUpdate(){
    	return sp.getBoolean(PREF_KEY_NEED_UPDATE, false);
    }
    
    public void setUpdateURL(String updateURL){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_UPDATE_URL, updateURL);
    	e.commit();
    }
    
    public String getUpdateURL(){
    	return sp.getString(PREF_KEY_UPDATE_URL, "");
    }
    
    public void setUpdateDescription(String updateDescription){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_UPDATE_DESCRIPTION, updateDescription);
    	e.commit();
    }
    
    public String getUpdateDescription(){
    	return sp.getString(PREF_KEY_UPDATE_DESCRIPTION, "");
    }
    
    public void setUpdateVersion(String updateVersion){
    	Editor e = sp.edit();
    	e.putString(PREF_KEY_UPDATE_VERSION, updateVersion);
    	e.commit();
    }
    
    public String getUpdateVersion(){
    	return sp.getString(PREF_KEY_UPDATE_VERSION, "");
    }
    
    public void setDesktopMessagesDot(long value){
    	Editor e = sp.edit();
    	e.putLong(PREF_KEY_DESKTOP_MESSAGES_DOT, value);
    	e.commit();
    }
    
    public long getDesktopMessagesDot(){
    	return sp.getLong(PREF_KEY_DESKTOP_MESSAGES_DOT, 0);
    }
    
    public void setDesktopMessagesDotServer(long value){
    	Editor e = sp.edit();
    	e.putLong(PREF_KEY_DESKTOP_MESSAGES_DOT_SERVER, value);
    	e.commit();
    }
    
    public long getDesktopMessagesDotServer(){
    	return sp.getLong(PREF_KEY_DESKTOP_MESSAGES_DOT_SERVER, 0);
    }
    
    public void setDesktopOrdersDot(long value){
    	Editor e = sp.edit();
    	e.putLong(PREF_KEY_DESKTOP_ORDERS_DOT, value);
    	e.commit();
    }
    
    public long getDesktopOrdersDot(){
    	return sp.getLong(PREF_KEY_DESKTOP_ORDERS_DOT, 0);
    }
    
    public void setDesktopOrdersDotServer(long value){
    	Editor e = sp.edit();
    	e.putLong(PREF_KEY_DESKTOP_ORDERS_DOT_SERVER, value);
    	e.commit();
    }
    
    public long getDesktopOrdersDotServer(){
    	return sp.getLong(PREF_KEY_DESKTOP_ORDERS_DOT_SERVER, 0);
    }

    public boolean getReceiverBillStatus(){
        return sp.getBoolean(PREF_KEY_RECEIVER_BILL , true);
    }

    public void setReceiverBillStatus(boolean status){
        Editor e = sp.edit();
        e.putBoolean(PREF_KEY_RECEIVER_BILL , status);
        e.commit();
    }

}
