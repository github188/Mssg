package com.fable.mssg.bean;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author wangmeng 2017/11/7
 */
@Data
@Builder
public class AuthInfo implements Serializable {

    private boolean isAuth;
    private String userId;
    private String comId;
    private int playType;
    private String reason;
}
