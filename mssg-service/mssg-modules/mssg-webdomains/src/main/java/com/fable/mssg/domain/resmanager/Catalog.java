package com.fable.mssg.domain.resmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.usermanager.SysUser;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 资源目录类
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Entity
@Table(name = "mssg_catalog")
@Data
public class Catalog extends AbstractUUIDPersistable{

    @Column(name = "CLOG_NAME", nullable = true, length = 50)
    private String catalogName;//目录名称

    @Column(name = "CLOG_CODE", nullable = true, length = 30)
    private String catalogCode;//目录编码

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "COM_ID", nullable = true,referencedColumnName = "id")
    private Company comId; //编目单位    外键

    @Column(name = "DESCRIPTION", nullable = true, length = 50)
    private String description;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "APPROVAL", nullable = true,referencedColumnName ="id")
    private SysUser approval;//审核人  外键

    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "CREATE_USER", nullable = true,referencedColumnName ="id")
    @ManyToOne
    private SysUser createUser;//创建人

    @Column(name = "CREATE_TIME", nullable = true)
    private Timestamp createTime;//创建时间

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "UPDATE_USER", nullable = true,referencedColumnName ="id")
    private SysUser updateUser;//更新人

    @Column(name = "UPDATE_TIME", nullable = true)
    private Timestamp updateTime;//更新时间

}
