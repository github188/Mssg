package com.fable.mssg.domain.businesslog;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.usermanager.SysUser;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "mssg_bus_log")
@Data
public class BusinessLog extends AbstractUUIDPersistable {

    public static final int REALTIME = 1;
    public static final int HISTORY = 2;
    public static final int REAL_CONTROL = 3;
    public static final int DOWNLOAD = 4;
    public static final int REAL_SNAP = 5;
    public static final int REAL_RECORD = 6;
    public static final int HIS_SNAP = 7;


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "COM_ID", referencedColumnName = "id")
    private Company companyId;  //公司id

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "DS_ID", referencedColumnName = "id")
    private DataSource deviceId;

    @Column(name = "VISIT_TYPE", length = 1)
    private int visitType;

    @Column(name = "START_TIME")
    private Timestamp startTime;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "VISIT_USER", referencedColumnName = "id")
    private SysUser visitUser;

    @Column(name = "SEGMENT_TIME", length = 10)
    private BigDecimal segmentTime;

    @Column(name = "OP_TYPE", length = 1)
    private Integer operateType;

    @Column(name="DS_NAME",length = 100)
    private String dsName;
}
