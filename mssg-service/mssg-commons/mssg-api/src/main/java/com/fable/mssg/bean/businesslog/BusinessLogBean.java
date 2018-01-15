package com.fable.mssg.bean.businesslog;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author: yuhl Created on 15:58 2017/9/21 0021
 */
@Data
public class BusinessLogBean implements Serializable {
    public static final int REALTIME=1;
    public static final int HISTORY=2;

    public String id;

    public String companyId;

    public String deviceId;

    public int visitType;

    public Timestamp startTime;

    public String visitUser;

    public int operateType;

    BigDecimal segmentTime;
}
