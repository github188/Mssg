package com.fable.mssg.bean.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: yuhl Created on 14:09 2017/11/2 0002
 */
@ApiModel("登录对象")
@Data
public class LoginInfo {

    @ApiModelProperty("登录名")
    public String loginName;

    @ApiModelProperty("密码")
    public String password;

    @ApiModelProperty("验证码")
    public String authCode;
}
