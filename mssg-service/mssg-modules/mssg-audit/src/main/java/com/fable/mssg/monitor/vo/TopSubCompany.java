package com.fable.mssg.monitor.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Description
 * @Author wangmeng 2017/10/26
 */
@Data
@Builder
public class TopSubCompany {
    String companyId;
    String companyName;
    String code;
    String subCount;
}
