package com.bjxapp.worker.utils;

import java.util.regex.Pattern;

/**
 * Created by zhangdan on 2018/11/9.
 * <p>
 * comments:
 */

public class CashReg {

    static String pattern = "^((0)|([1-9][0-9]*)|(([0]\\\\.\\\\d{0,2}|[1-9][0-9]*\\\\.\\\\d{0,2})))$";

    public static boolean isCashValid(String cash) {

        boolean isMatch = Pattern.matches(pattern, cash);

        return isMatch;
    }
}
