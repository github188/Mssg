package com.fable.mssg.user.vo;

import lombok.Data;

/**
 * @Description  用户修改密码对象
 * @Author xiejk 2017/12/08
 */
@Data
public class SysUserPwd {
    String oldpwd;
    String newpwd;
}
