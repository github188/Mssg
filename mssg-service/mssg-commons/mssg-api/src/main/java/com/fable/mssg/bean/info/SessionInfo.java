package com.fable.mssg.bean.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: yuhl Created on 14:40 2017/11/2 0002
 */
@ApiModel("会话信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfo implements Serializable {

    @ApiModelProperty("凭证")
    public String token;

    @ApiModelProperty("登录用户")
    LoginUserInfo loginUserInfo;
}
