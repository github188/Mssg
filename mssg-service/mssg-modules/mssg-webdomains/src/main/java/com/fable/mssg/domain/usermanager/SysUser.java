package com.fable.mssg.domain.usermanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.company.Company;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户实体类
 * @Description
 * @Author wangmeng 2017/9/20
 */
@Entity
@Table(name = "mssg_sys_user")
@Data
public class SysUser extends AbstractUUIDPersistable {

    @Column(name = "LOGIN_NAME", nullable = false, length = 20)
    private String loginName;//登录名称
    @Column(name = "USER_NAME", nullable = false, length = 50)
    private String userName;//用户名称
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;//密码
    @Column(name = "SALT", nullable = false, length = 255)
    private String salt;//md5加密

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "ROLE_ID",referencedColumnName = "ID")
    private SysRole roleId;//角色id

    @Column(name = "STATE", nullable = false)
    private int state;//用户状态
    @Column(name = "ID_CARD", nullable = true, length = 18)
    private String idCard;//用户身份证号
    @Column(name = "TELPHONE", nullable = true, length = 20)
    private String telphone;//电话
    @Column(name = "EMAIL", nullable = true, length = 50)
    private String email;//电子邮箱
    @Column(name = "DESCRIPTION", nullable = true, length = 100)
    private String description;//描述
    @Column(name = "DELETE_FLAG", nullable = true)
    private Integer deleteFlag;//是否被删除
    @Column(name = "LOGIN_STATE", nullable = true)
    private Integer loginState;//登录状态
    @Column(name = "CELL_PHONE_NUMBER", nullable = true, length = 50)
    private String cellPhoneNumber;//手机号码
    @Column(name = "CREATE_USER", length = 36)
    private String createUser;//创建人
    @Column(name = "CREATE_TIME", nullable = false)
    private Timestamp createTime;//创建时间
    @Column(name = "UPDATE_USER", nullable = true, length = 36)
    private String updateUser;//更新人
    @Column(name = "UPDATE_TIME", nullable = true)
    private Timestamp updateTime;//更新时间
    @Column(name = "POSITION")
    private  String position; //职务

    @Column(name ="SIPID")  //sipid
    private String sipId;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "COM_ID",referencedColumnName = "id")
    private Company comId;  //公司id

    public SysUser(String id) {
        super.setId(id);
    }

    public SysUser(){}

    public String getUserName() {
        return userName;
    }

}
