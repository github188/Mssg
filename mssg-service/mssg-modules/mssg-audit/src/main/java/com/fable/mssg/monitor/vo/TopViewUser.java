package com.fable.mssg.monitor.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/10/26
 */
@Data
@Builder
public class TopViewUser {
    String username;
    String companyName;
    Integer realtime;
    Integer history;
    Integer preview;

}
