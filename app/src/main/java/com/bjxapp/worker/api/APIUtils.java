package com.bjxapp.worker.api;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

public class APIUtils {
    public static Map<String, String> getBasicParams(Context context) {
        Map<String, String> params = new HashMap<String, String>();
        
        /*
        String sid = "1";
        if (Utils.isNotEmpty(sid)) {
            params.put(URL_SID_PARAM, sid);
        }

        params.put(URL_UUID_PARAM, "0000000000");
        params.put(URL_PM_PARAM, Env.getModels());
        params.put(URL_SYS_PARAM, Env.getSDK());
        params.put(URL_APP_VER_PARAM, Env.getVersion(context));
        
        String channel = "99999999";
        if (Utils.isNotEmpty(channel)) {
            params.put(URL_CHANNEL_PARAM, channel);
        }

        String language = "cn";
        params.put(URL_LANGUAGE_PARAM, language);

        params.put(URL_PLATFORM_PARAM, "0");

        String userId = "1";
        if (Utils.isNotEmpty(userId)) {
            params.put(URL_USER_ID_PARAM, userId);
        }
        */
        
        return params;
    }
}
