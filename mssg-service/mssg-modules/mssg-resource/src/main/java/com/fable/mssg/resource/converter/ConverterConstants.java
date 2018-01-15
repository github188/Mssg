package com.fable.mssg.resource.converter;

import java.text.SimpleDateFormat;

/**
 * @Description
 * @Author wangmeng 2017/10/19
 */
public class ConverterConstants {
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String noCompany = "No Company";




    //最好是读配置文件
    public static String getType(Integer number) {
        switch (number) {
            case 9:
                return "视频";
            default:
                return "";
        }
    }
    //资源状态 1待提交 2:待审核, 3:待发布 ,4 :已拒绝, 5:已发布,6:已撤销
    public static String getResourceStatus(Integer state) {
        switch (state) {
            case 1:
                return "待提交";
            case 2:
                return "待审核";
            case 3:
                return "待发布";
            case 4:
                return "已拒绝";
            case 5:
                return "已发布";
            case 6:
                return "已撤销";
            default:
                return "";
        }
    }

    public static String getResourceLevel(Integer level) {
        switch (level) {
            case 0:
                return "完全共享";
            case 1:
                return "部分共享";
            default:
                return "";
        }
    }
}
