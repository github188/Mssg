package com.fable.mssg.service.onlinelog;


import com.fable.mssg.domain.dsmanager.DeviceVist;
import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.usermanager.SysUser;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;

/**
 * description  用户访问记录数接口
 * @author xiejk 2017/10/26
 */
public interface OnLineLogService {

    /**
     *条件查询用户访问记录表
     * @param userName  用户名称
     * @param companyName 公司名称
     * @param loginTime 登录时间
     * @param logoutTime 下线时间
     * @return  List<OnLineLog>
     */
    Page<OnLineLog> findAllOnLineLogByCondition(String userName, String companyName,Integer page,Integer size,
                                                Timestamp loginTime, Timestamp logoutTime);

    /**
     * 强制用户下线
     * @param
     * @return 是否成功
     */
    boolean forceUserOffline(SysUser user,OnLineLog onLineLog);




    /**
     * 查询单个访问日志
     * @param onLineId 访问记录id
     * @return List<VDeviceVist>
     */
    OnLineLog findOneOnline(String onLineId);

    OnLineLog findOneOnline(String sysUserId,int isToffine);

    OnLineLog findOneOnline(SysUser sysUser,Timestamp loginOutTime);

    //用户开始访问设计记录接口 记录访问设备数  查找用户id  查询下线时间为null的
    void addvistEquipCount(String userid);


    //用户下线记录
    OnLineLog loginout(String id,Long onlineRate);

    //用户登录时添加访问记录数
    OnLineLog addOnlineLog(OnLineLog onLineLog);

    void updateLoginout(Timestamp loginoutTime,String id);



}
