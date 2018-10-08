/**
 * @brief     Package com.ijinshan.browser.utils
 * @author    zhouchenguang
 * @since     1.0.0.0
 * @version   1.0.0.0
 * @date      2012-12-10
 */

package com.bjxapp.worker.http.keyboard.commonutils;


import android.util.Log;

/**
 * @file KLog.java
 * @brief This file is part of the Utils module of KBrowser project. \n
 * This file serves as "java" source file that presents common log
 * utilities that would be required by all of the modules. \n
 * @author zhouchenguang
 * @version 1.0.0.0
 * @date 2012-12-10
 * <p/>
 * \if TOSPLATFORM_CONFIDENTIAL_PROPRIETARY
 * ============================================================================\n
 * \n
 * Copyright (c) 2012 zhouchenguang.  All Rights Reserved.\n
 * \n
 * ============================================================================\n
 * \n
 * Update History\n
 * \n
 * Author (Name[WorkID]) | Modification | Tracked Id | Description\n
 * --------------------- | ------------ | ---------- | ------------------------\n
 * zhouchenguang[7897]   |  2012-12-10  | <xxxxxxxx> | Initial Created.\n
 * \n
 * \endif
 * <p/>
 * <tt>
 * \n
 * Release History:\n
 * \n
 * Author (Name[WorkID]) | ModifyDate | Version | Description \n
 * --------------------- | ---------- | ------- | -----------------------------\n
 * zhouchenguang[7897]   | 2012-12-10 | 1.0.0.0 | Initial created. \n
 * \n
 * </tt>
 * @since 1.0.0.0
 */
//=============================================================================
//                                IMPORT PACKAGES
//=============================================================================

//=============================================================================
//                               CLASS DEFINITIONS
//=============================================================================

/**
 * @class KLog
 * @brief Class for present common log utilities. \n
 * @author zhouchenguang
 * @since 1.0.0.0
 * @version 1.0.0.0
 * @date 2012-12-10
 * @par Applied: External
 */
public class KLog {
    // -------------------------------------------------------------------------
    // Public static members
    // -------------------------------------------------------------------------
    /**
     * @brief logException priority of none.
     */
    public static final int PRIORITY_NONE = 0xFFFF;

    /**
     * @brief logException priority of verbose.
     */
    public static final int PRIORITY_VERBOSE = 2;
    /**
     * @brief logException priority of debug.
     */
    public static final int PRIORITY_DEBUG = 3;
    /**
     * @brief logException priority of info.
     */
    public static final int PRIORITY_INFO = 4;
    /**
     * @brief logException priority of warning.
     */
    public static final int PRIORITY_WARN = 5;
    /**
     * @brief logException priority of error.
     */
    public static final int PRIORITY_ERROR = 6;
    /**
     * @brief logException priority of exception.
     */
    public static final int PRIORITY_ASSERT = 7;

    public static boolean sDEBUG = false;
    
    public static final int RESULT_SUCCESS = 0;
    // -------------------------------------------------------------------------
    // Private static members
    // -------------------------------------------------------------------------
    /**
     * @brief logException tag.
     */
    private static final String TAG = "CMKeyboard: ";

    private static int msLogPriority = sDEBUG ? PRIORITY_DEBUG : PRIORITY_NONE; // default

// TODO Remove unused code found by UCDetector
//     public static boolean isLogEnable(int priority) {
//         return (msLogPriority <= priority);
//     }

    // -------------------------------------------------------------------------
    // Public static member methods
    // -------------------------------------------------------------------------
// TODO Remove unused code found by UCDetector
//     public static int getLogPriority() {
//         return msLogPriority;
//     }

    public static void setLogPriority(int priority) {
        msLogPriority = priority;
    }

    // -------------------------------------------------------------------------
    /**
     * @brief logException for debug info.
     * @par Sync (or) Async: This is a Synchronous function.
     * @param tag log tag that mentioned module name.\n
     * @param message log info.\n
     * @return Result.
     * @author zhouchenguang
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    public static int d(String tag, String message) {
        if (msLogPriority <= PRIORITY_DEBUG) {
            if (message == null) {
                message = "";
            }
            return Log.d(TAG + tag, message);
        } else {
            return RESULT_SUCCESS;
        }
    }

    public static int d(String tag, String message, Throwable tr) {
        if (msLogPriority <= PRIORITY_DEBUG) {
            if (message == null) {
                message = "";
            }
            return Log.d(TAG + tag, message, tr);
        } else {
            return RESULT_SUCCESS;
        }
    }

    /**
     * 防止先计算后输出，定义可变参的log方法，在关闭debug属性后，可以提高效率
     * 
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int d(String tag, String format, Object... args) {
        if (msLogPriority <= PRIORITY_DEBUG) {
            String msg = String.format(format, args);
            return Log.d(TAG + tag, msg);
        } else {
            return RESULT_SUCCESS;
        }
    }

    // -------------------------------------------------------------------------
    /**
     * @brief logException for error info.
     * @par Sync (or) Async: This is a Synchronous function.
     * @param tag log tag that mentioned module name.\n
     * @param e exception to be shown.\n
     * @param message log info.\n
     * @return KResult.
     * @author zhouchenguang
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    public static int exception(String tag, Exception e, String message) {
        if (msLogPriority <= PRIORITY_ERROR) {
            if (message == null) {
                message = "";
            }
            return Log.e(TAG + tag, message, e);
        } else {
            return RESULT_SUCCESS;
        }
    }

    // -------------------------------------------------------------------------
    /**
     * @brief logException for error info.
     * @par Sync (or) Async: This is a Synchronous function.
     * @param tag log tag that mentioned module name.\n
     * @param message log info.\n
     * @return KResult.
     * @author zhouchenguang
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    public static int e(String tag, String message) {
        if (msLogPriority <= PRIORITY_ERROR) {
            if (message == null) {
                message = "";
            }
            return Log.e(TAG + tag, message);
        } else {
            return RESULT_SUCCESS;
        }
    }

    /**
     * 打印错误日志，防止先计算后输出，定义可变参的log方法，在关闭debug属性后，可以提高效率
     * 
     * @param tag
     * @param format
     * @param args
     * @return
     * @author caisenchuan
     */
    public static int e(String tag, String format, Object... args) {
        if (msLogPriority <= PRIORITY_ERROR) {
            String msg = String.format(format, args);
            return Log.e(TAG + tag, msg);
        } else {
            return RESULT_SUCCESS;
        }
    }

    public static int e(String tag, String message, Throwable tr) {
        if (msLogPriority <= PRIORITY_ERROR) {
            if (message == null) {
                message = "";
            }
            return Log.e(TAG + tag, message, tr);
        } else {
            return RESULT_SUCCESS;
        }
    }

    // -------------------------------------------------------------------------
    /**
     * @brief logException for info info.
     * @par Sync (or) Async: This is a Synchronous function.
     * @param tag log tag that mentioned module name.\n
     * @param message log info.\n
     * @return KResult.
     * @author zhouchenguang
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    public static int i(String tag, String message) {
        if (msLogPriority <= PRIORITY_INFO) {
            if (message == null) {
                message = "";
            }
            return Log.i(TAG + tag, message);
        } else {
            return RESULT_SUCCESS;
        }
    }

    /**
     * 防止先计算后输出，定义可变参的log方法，在关闭debug属性后，可以提高效率
     * 
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int i(String tag, String format, Object... args) {
        if (msLogPriority <= PRIORITY_INFO) {
            String msg = String.format(format, args);
            return Log.d(TAG + tag, msg);
        } else {
            return RESULT_SUCCESS;
        }
    }

    // -------------------------------------------------------------------------
    /**
     * @brief logException for warning info.
     * @par Sync (or) Async: This is a Synchronous function.
     * @param tag log tag that mentioned module name.\n
     * @param message log info.\n
     * @return KResult.
     * @author zhouchenguang
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    public static int w(String tag, String message) {
        if (msLogPriority <= PRIORITY_WARN) {
            if (message == null) {
                message = "";
            }
            return Log.w(TAG + tag, message);
        } else {
            return RESULT_SUCCESS;
        }
    }

    /**
     * 防止先计算后输出，定义可变参的log方法，在关闭debug属性后，可以提高效率
     * 
     * @param tag
     * @param format
     * @param args
     * @return
     */
    public static int w(String tag, String format, Object... args) {
        if (msLogPriority <= PRIORITY_WARN) {
            String msg = String.format(format, args);
            return Log.w(TAG + tag, msg);
        } else {
            return RESULT_SUCCESS;
        }
    }

    /**
     * 打印警告，防止先计算后输出，定义可变参的log方法，在关闭debug属性后，可以提高效率
     * 
     * @param tag
     * @param message
     * @param tr
     * @return
     */
    public static int w(String tag, String message, Throwable tr) {
        if (msLogPriority <= PRIORITY_WARN) {
            if (message == null) {
                message = "";
            }
            return Log.w(TAG + tag, message, tr);
        } else {
            return RESULT_SUCCESS;
        }
    }

    // -------------------------------------------------------------------------
    /**
     * @brief logException for verbose info.
     * @par Sync (or) Async: This is a Synchronous function.
     * @param tag log tag that mentioned module name.\n
     * @param message log info.\n
     * @return KResult.
     * @author zhouchenguang
     * @since 1.0.0.0
     * @version 1.0.0.0
     * @par Prospective Clients: External Classes
     */
    public static int v(String tag, String message) {
        if (msLogPriority <= PRIORITY_VERBOSE) {
            if (message == null) {
                message = "";
            }
            return Log.v(TAG + tag, message);
        } else {
            return RESULT_SUCCESS;
        }
    }

    // -------------------------------------------------------------------------
// TODO Remove unused code found by UCDetector
//     /**
//      * @brief logException for indicate priority.
//      * @par Sync (or) Async: This is a Synchronous function.
//      * @param [IN] priority indicate log priority.\n
//      * @param [IN] tag log tag that mentioned module name.\n
//      * @param [IN] message log info.\n
//      * @return KResult.
//      * @author zhouchenguang
//      * @since 1.0.0.0
//      * @version 1.0.0.0
//      * @par Prospective Clients: External Classes
//      */
//     public static int println(int priority, String tag, String message) {
//         if (message == null) {
//             message = "";
//         }
//         return logException.println(priority, sTAG + tag, message);
//     }

    private static long sLogStamp = -1L;
    public static void startupLog() {
        if (!sDEBUG) {
            return;
        }
        long timeMillis = System.currentTimeMillis();
        if (sLogStamp == -1) {
            sLogStamp = timeMillis;
        }
        try {
            StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            String[] className = element.getClassName().split("\\.");
            String method = element.getMethodName();
            int line = element.getLineNumber();
            Log.e("StartUp", className[className.length - 1] + "::" + method + ", line:" + line + ", elapsed:" + (timeMillis - sLogStamp));
        } catch (Exception e) {
        }
        sLogStamp = timeMillis;
    }
}
