package com.fable.mssg.monitor.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/10/26
 */
@Data
@Builder
public class TopResSurvey {
    String resourceId;
    String resourceName;
    String dsQty;
    String realtime;
    String history;
    String subscribe;
    String createTime;
}
