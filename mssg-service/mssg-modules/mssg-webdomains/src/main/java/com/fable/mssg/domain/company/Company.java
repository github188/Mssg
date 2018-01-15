package com.fable.mssg.domain.company;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 公司实体类
 * @Description
 * @Author wangmeng 2017/9/18
 */
@Entity
@Table(name = "mssg_company")
@Data
public class Company extends AbstractUUIDPersistable{


    @Column(name = "NAME", nullable = true, length = 50)
    private String name;//公司名称

    @Column(name = "CODE", nullable = true, length = 50)
    private String code;//公司编码

    @Column(name = "PID", nullable = true, length = 36)
    private String pid;//父id

    @Column(name = "LEVEL", nullable = true)
    private Integer level;//

    @Column(name = "DESCRIPTION", nullable = true, length = 2000)
    private String description;//公司描述

    @Column(name = "IDEX", nullable = true)
    private Integer idex;//排序编号

    @Column(name = "EMAIL", nullable = true, length = 128)
    private String email;//电子邮箱

    @Column(name = "ADDRESS", nullable = true, length = 256)
    private String address;//公司地址

    @Column(name = "TELPHONE", nullable = true, length = 11)
    private String telphone;//公司电话

    @Column(name = "OFFICE_PHONE")
    private String officePhone;

    @Column(name = "CONTACTS", nullable = true, length = 64)
    private String contacts;//联系人

    @Column(name="POSITION",length = 50)
    private String position;

    @Column(name="COM_IP_SEGMENT",length = 50)
    private String comIpSegment;

    @Column(name="COM_TYPE",length = 36)
    private Integer comType;//1.管理单位 2.共享单位

    @Column(name="COM_LEVEL")
    private Integer comLevel;

    @Column(name="STATUS")
    private Integer status;//公司的状态

    @Column(name = "CREATE_TIME", nullable = true)
    private Timestamp createTime;//创建时间

    @Column(name = "CREATE_USER", nullable = true, length = 64)
    private String createUser;//创建人

    @Column(name = "UPDATE_TIME", nullable = true)
    private Timestamp updateTime;//更新时间

    @Column(name = "UPDATE_USER", nullable = true,length = 128)
    private String updateUser;//更新人

    public Company(){}
    public Company(String id){
        super.setId(id);
    }
}
