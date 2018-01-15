package com.fable.mssg.resource.vo;

import lombok.Builder;
import lombok.Data;

/**
 * description 用户角色视图类
 * @author xiejk 2017/9/20
 */
@Builder
@Data
public class VRole {
    //用户id
    private String id;
    //角色名称
    private String roleName;
    //角色描述
    private String role_decription;

    private String roleCode;

}
