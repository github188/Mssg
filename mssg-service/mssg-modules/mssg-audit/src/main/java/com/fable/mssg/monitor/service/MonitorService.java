package com.fable.mssg.monitor.service;

import com.fable.mssg.monitor.vo.*;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/10/25
 */
public interface MonitorService {
    String TYPE_VIEW = "view";
    String TYPE_SUBMIT = "submit";

    /**
     * 获取资源概况
     * @return
     */
    SystemSurvey getSystemSurvey();

    /**
     * 获取进出流量
     * @return
     */
    Traffic getTraffic();

    /**
     * 获取最高订阅的单位
     * @return
     */
    List<TopSubCompany> getTopSubCompany(Integer days);

    /**
     * 最热资源
     * @param days 前n天
     * @param type submit/view
     * @return
     */
    List<TopResSurvey> getTopResource(Integer days, String type,Integer size);

    /**
     * 获取观看次数最多的用户
     * @param unit
     * @return
     */
    List<TopViewUser> getTopViewUser(Integer unit,Integer size);
}
