package com.fable.mssg.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: yuhl Created on 10:37 2017/11/24 0024
 */
public class DateUtils {

    /**
     * 获取当前日期-年月日
     * @return
     */
    public static String currentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());
        return currentDate;
    }

    /**
     * 获取当前时间-时分秒
     * @return
     */
    public static String currentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = simpleDateFormat.format(new Date());
        return currentTime;
    }

    /**
     * 获取当前时间-年月日 时分秒
     * @return
     */
    public static String current() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current = simpleDateFormat.format(new Date());
        return current;
    }

}
