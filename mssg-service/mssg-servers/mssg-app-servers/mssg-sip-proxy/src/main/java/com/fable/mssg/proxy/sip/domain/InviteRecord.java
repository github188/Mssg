package com.fable.mssg.proxy.sip.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * TODO 在审计模块(mssg-audit)里写
 */
public class InviteRecord {
    @Setter
    @Getter
    String id;

    @Setter
    @Getter
    String companyId;

    @Setter
    @Getter
    String deviceId;

    @Setter
    @Getter
    int visitType;

    @Setter
    @Getter
    Timestamp startTime;

    @Setter
    @Getter
    String visitUser;

    @Setter
    @Getter
    BigDecimal segmentTime;
}
