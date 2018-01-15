package com.fable.mssg.bean.info;

import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.usermanager.SysMenu;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: yuhl Created on 14:42 2017/11/2 0002
 */
@ApiModel("用户基本信息")
@Data
public class LoginUserInfo implements Serializable {

    @ApiModelProperty("主键")
    String id;

    @ApiModelProperty("登录名")
    String loginName;

    @ApiModelProperty("姓名")
    String userName;

    @ApiModelProperty("系统用户")
    SysUser sysUser;

    @ApiModelProperty("角色")
    SysRole role;

    @ApiModelProperty("菜单")
    List<SysMenu> menuList;

    @ApiModelProperty("登录日志")
    OnLineLog onLineLog;
}
