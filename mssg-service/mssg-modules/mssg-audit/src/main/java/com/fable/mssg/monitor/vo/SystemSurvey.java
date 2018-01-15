package com.fable.mssg.monitor.vo;

import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/10/25
 */
@Data
public class SystemSurvey {

    long resource;
    long subscribe;
    long realtime;
    long history;
    long totalUser;
    long loginUser;
    long onlineUnit;
    long sharedUnit;
}


