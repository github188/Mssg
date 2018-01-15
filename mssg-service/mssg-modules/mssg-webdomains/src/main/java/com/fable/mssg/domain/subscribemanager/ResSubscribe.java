package com.fable.mssg.domain.subscribemanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;

import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.resmanager.Resource;
import com.fable.mssg.domain.usermanager.SysUser;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

/**
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Entity
@Table(name = "mssg_res_subscribe")
@Data
public class ResSubscribe extends AbstractUUIDPersistable {

    @Column(name = "LINK_MAN", nullable = true,length = 50)
    private String linkMan;

    @Column(name = "TEL_PHONE", nullable = true, length = 50)
    private String telPhone;

    @Column(name = "CELL_PHONE",length = 50)
    private String cellPhone;

    @Column(name = "DUTY",length = 50)
    private String duty;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "COM_ID", nullable = true ,referencedColumnName ="id")
    private Company comId;

    @ManyToOne
    @JoinColumn(name = "RES_ID", nullable = true ,referencedColumnName ="id")
    private Resource resId;

    @Column(name = "STATE", nullable = true)
    private Integer state;//资源状态  (1:待审核,2:已审核,3:已拒绝,4:去订阅)

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "APPLY_USER", nullable = true,referencedColumnName ="id")
    private SysUser applyUser; //申请人

    @Column(name = "APPLY_TIME", nullable = true)
    private Timestamp applyTime;

    @Column(name = "APPLY_CAUSE", nullable = true, length = 1000)
    private String applyCause;

    @Column(name = "APPLY_DOC_PATH",nullable = true)
    private String applyDocPath;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "APPROVAL_USER", nullable = true,referencedColumnName ="id")
    private SysUser approvalUser; //审核人

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "CREATE_USER", nullable = true,referencedColumnName ="id")
    private SysUser createUser;

    @Column(name = "CREATE_TIME", nullable = true)
    private Timestamp createTime;

    @ManyToOne
    @JoinColumn(name = "UPDATE_USER", nullable = true,referencedColumnName ="id")
    private SysUser updateUser;

    @Column(name = "UPDATE_TIME", nullable = true)
    private Timestamp updateTime;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="RES_SUBSCRIBE_ID")
    Set<SubscribePrv> subscribePrvList;
}
