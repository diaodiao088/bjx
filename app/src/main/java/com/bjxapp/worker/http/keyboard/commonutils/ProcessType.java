package com.bjxapp.worker.http.keyboard.commonutils;

public class ProcessType {
    public static final int UNKNOWN = -1;
    public static final int MAIN = 0;
    public static final int CRASH_SERVICE = 1;
    public static final int DOWNLOAD_SERVICE = 2;
    public static final int GCM_SERVICE = 3;
    public static final int THEME = 4;
    public static final int ALIVE = 5;
    public static final int WIZARD = 6;
    public static final int BADGE = 7;
    public static final int GAME = 8;

    // The follow process names must keep sync with names defined in
    // AndroidManifest.xml
    private static final String CRASH_SERVICE_PROCESS_NAME_SUFFIX = ":crash_service";
    private static final String DOWNLOAD_SERVICE_PROCESS_NAME_SUFFIX = ":download";
    private static final String GCM_SERVICE_PROCESS_NAME_SUFFIX = ":gcm_service";
    private static final String THEME_PROCESS_NAME_SUFFIX = ":theme";
    private static final String ALIVE_REPORT_PROCESS_NAME_SUFFIX =":alive";
    private static final String WIZARD_REPORT_PROCESS_NAME_SUFFIX =":wizard";
    private static final String BADGE_PROCESS_NAME_SUFFIX =":badge";
    private static final String GAME_H5 = ":game";

    public static int getProcessType(String processName, String packageName) {
        int type = UNKNOWN;
        if(processName.equals(packageName))
            type = MAIN;
        else if (processName.indexOf(CRASH_SERVICE_PROCESS_NAME_SUFFIX) > 0)
            type = CRASH_SERVICE;
        else if (processName.indexOf(DOWNLOAD_SERVICE_PROCESS_NAME_SUFFIX) > 0)
            type = DOWNLOAD_SERVICE;
        else if (processName.indexOf(GCM_SERVICE_PROCESS_NAME_SUFFIX) > 0)
            type = GCM_SERVICE;
        else if (processName.indexOf(THEME_PROCESS_NAME_SUFFIX) >0)
            type = THEME;
        else if(processName.indexOf(ALIVE_REPORT_PROCESS_NAME_SUFFIX)>0)
            type = ALIVE;
        else if (processName.indexOf(WIZARD_REPORT_PROCESS_NAME_SUFFIX)>0)
            type = WIZARD;
        else if (processName.indexOf(BADGE_PROCESS_NAME_SUFFIX)>0)
            type = BADGE;
        else if (processName.indexOf(GAME_H5) > 0) {
            type = GAME;
        }
        return type;
    }

    public static String getShortProcessName(String processName) {
        String[] items = processName.split(":");
        if (items.length == 2)
            return items[1];
        return "";
    }
}
