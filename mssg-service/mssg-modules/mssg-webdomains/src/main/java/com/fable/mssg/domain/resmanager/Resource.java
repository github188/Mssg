package com.fable.mssg.domain.resmanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.dsmanager.DataSource;
import com.fable.mssg.domain.dictmanager.DictItem;
import com.fable.mssg.domain.subscribemanager.ResSubscribe;
import com.fable.mssg.domain.usermanager.SysUser;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Entity
@Table(name = "mssg_resource")
@Data
public class Resource extends AbstractUUIDPersistable{

    @Column(name = "RES_CASE", nullable = true, length = 2000)
    private String resCase;  //编目原因

    @Column(name = "RES_TYPE", nullable = true)
    private Integer resType;//资源类型

    @Column(name = "LINK_MAN", nullable = true,length = 50)
    private String linkMan;//联系人  长度50 不是外键

    //电话
    @Column(name = "TEL_PHONE", length = 50)
    private String telPhone;

    @Column(name = "RES_NAME", nullable = true, length = 100)
    private String resName;//资源名称

    //资源英文名称
    @Column(name = "RES_ENG_NAME", length = 50)
    private String resEngName;

    //资源代码
    @Column(name = "RES_CODE", length = 50)
    private String resCode;

    //资源等级
    @Column(name = "RES_LEVEL")
    private Integer resLevel;

    @Column(name = "ICON_ROOT", nullable = true, length = 100)
    private String iconRoot;//资源图片路径

    @Column(name = "RES_ABSTRACT", nullable = true, length = 200)
    private String resAbstract;//资源描述

    @Column(name = "MAIN_CATEGORY")
    private Integer mainCategory;//资源主题


    @Column(name = "HY_CATEGORY")
    private Integer hyCategory;//行业主题

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "APPROVAL_MAN",referencedColumnName ="ID")
    private SysUser approvalMan;

    //资源状态
    @Column(name = "RES_STATUS")
    private Integer resStatus;

    //资源目录   外键
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "CLOG_ID",referencedColumnName ="ID")
    private Catalog catalogId;

    //创建人   外键
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "CREATE_USER",referencedColumnName ="ID")
    private SysUser createUser;

    //创建时间
    @Column(name = "CREATE_TIME")
    private Timestamp createTime;

    //更新人  外键
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "UPDATE_USER", referencedColumnName ="ID")
    private SysUser updateUser;

    //  更新时间
    @Column(name = "UPDATE_TIME")
    private Timestamp updateTime;

    //数据源信息
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(mappedBy = "rsId")
    private List<DataSource> das;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RES_CONFIG_ID")
    private ResourceConfig resourceConfig;


    @OneToMany(fetch = FetchType.EAGER,mappedBy = "resId")
    private List<ResSubscribe> resSubscribes;

    public Resource(){}
    public Resource(String id){
        super.setId(id);
    }

//    @ManyToOne
//    @JoinColumn
//    private DataSource datasourceRootId;

}
