package com.fable.mssg.resource.vo;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户访问日志视图类
 * @author xiejk 2017/10/26
 */
@Builder
@Data
public class VOnLineLog {
    //用户名
    String userName;
    //登录ip
    String loginIp;
    //单位名称
    String companyName;
    //流出流量
    Long onlineRate;
    //访问设备数
    int visitCount;
    //上线时间
    String loginTime;
    //下线时间
    String  logoutTime;
    //id
    String id;
    //是否强制下线
    int istOffline;
    //用户id
    String userId;
    //用户登录名
    String loginName;
}
