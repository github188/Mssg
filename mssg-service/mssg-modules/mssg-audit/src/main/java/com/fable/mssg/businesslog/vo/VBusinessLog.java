package com.fable.mssg.businesslog.vo;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Builder
@Data
@ApiModel("业务日志")
public class VBusinessLog {
    public static final int REALTIME=1;
    public static final int HISTORY=2;
    String companyID;
    String deviceID;
    String visitType;//对应操作类型 不是访问类型
    String startTime;
    String visitUser;
    BigDecimal segmentTime;
    String operateType;
    String dsName;
}
