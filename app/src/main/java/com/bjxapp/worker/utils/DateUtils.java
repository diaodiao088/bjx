package com.bjxapp.worker.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhangdan on 2018/10/10.
 * comments:
 */

public class DateUtils {

    /**
     * 当前天数
     *
     * @param n
     * @return
     */
    public static String addDay(int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cd = Calendar.getInstance();
            cd.setTime(new Date());
            cd.add(Calendar.DATE, n);//增加一天

            return sdf.format(cd.getTime());

        } catch (Exception e) {
            return null;
        }

    }


}

