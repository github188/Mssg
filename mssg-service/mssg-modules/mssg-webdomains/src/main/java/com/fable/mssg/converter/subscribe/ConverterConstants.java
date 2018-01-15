package com.fable.mssg.converter.subscribe;

import java.text.SimpleDateFormat;

/**
 * @Description
 * @Author wangmeng 2017/10/19
 */
public class ConverterConstants {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String noRes = "No Resource";
    public static String noCompany = "No Company";
    public static String noUser = "No User";
    public static String noDataSource = "No DataSource";




    //最好是读配置文件
    public static String getType(Integer number) {
        switch (number) {
            case 9:
                return "视频";
            default:
                return "";
        }
    }

    public static String getSubscribeStatus(Integer state) {
        switch (state) {
            case 1:
                return "未审核";
            case 2:
                return "通过审核";
            case 3:
                return "已拒绝";
            case 4:
                return "已取消";
            default:
                return "";
        }
    }

}
