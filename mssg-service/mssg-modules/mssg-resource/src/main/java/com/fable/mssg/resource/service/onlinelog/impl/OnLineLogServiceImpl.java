package com.fable.mssg.resource.service.onlinelog.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.resource.repository.onlinelog.OnLineLogRepository;
import com.fable.mssg.service.onlinelog.OnLineLogService;
import com.fable.mssg.user.repository.SysUserRepository;
import com.fable.mssg.user.vo.SysUserLoginStatus;
import com.fable.mssg.user.vo.SysUserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.List;

/**
 * description 用户接口实现类
 * @author xiejk 2017/10/26
 */
@Service
@Exporter(interfaces = OnLineLogService.class)
@Slf4j
public class OnLineLogServiceImpl implements OnLineLogService {

    //用户访问记录dao层操作对象
    @Autowired
    OnLineLogRepository onLineLogRepository;

    //用户dao层操作对象
    @Autowired
    SysUserRepository sysUserRepository;

    @PostConstruct
    public void init(){
        log.info("开始初始化在线日志,将未下线的日志下线");

        List<OnLineLog> onLineLogs = onLineLogRepository.findByLogoutTimeIsNull();
        for(OnLineLog onLineLog:onLineLogs){
            onLineLog.setLogoutTime(new Timestamp(System.currentTimeMillis()));
        }
        onLineLogRepository.save(onLineLogs);
        log.info("在线日志初始化完毕");
        log.info("开始初始化用户登录状态,将所有用户修改为下线状态");
        List<SysUser> sysUsers=sysUserRepository.findByLoginState(1);
        for(SysUser s:sysUsers){
            s.setLoginState(0);
        }
        sysUserRepository.save(sysUsers);
        log.info("初始化用户登录状态完毕");
    }


    /**
     *条件查询用户访问记录表（共享端）
     * @param userName  用户名称
     * @param companyName 公司名称
     * @param loginTime 登录时间
     * @param logoutTime 下线时间
     * @return  List<OnLineLog>
     */
    @Override
    public Page<OnLineLog> findAllOnLineLogByCondition(String userName, String companyName,Integer page,Integer size,
                                                       Timestamp loginTime, Timestamp logoutTime) {
        Page<OnLineLog>  list;
        list= onLineLogRepository.findAll(
                //lambda 表达式写法
                (root, query, cb) -> {
                    Predicate p1 = cb.equal(root.get("user").get("roleId").get("roleType"),"2");//2标识共享端
                    //加上第一个条件模糊查询
                    if(null!=userName&&!"".equals(userName)){
                        Predicate  predicate= cb.like(root.get("user").get("loginName"),"%"+userName+"%");
                        if(p1!=null){
                            p1=cb.and(p1,predicate);
                        }else{
                            p1=predicate;
                        }
                    }
                    //加上第二个条件 catalogid
                    if(null!=companyName&&!"".equals(companyName)){
                        Predicate predicate=cb.like(root.get("user").get("comId").get("name"),"%"+companyName+"%");
                        if(p1!=null){
                            p1=cb.and(p1,predicate);
                        }else{
                            p1=predicate;
                        }
                    }
                    // 搜索时间登录时间
                    if(null!=loginTime){
                        Predicate predicate=cb.greaterThan(root.get("loginTime"),loginTime);
                        if(p1!=null){
                            p1=cb.and(p1,predicate);
                        }else{
                            p1=predicate;
                        }
                    }
                    // 搜索时间下线时间
                    if(null!=loginTime){
                        Predicate predicate=cb.lessThan(root.get("logoutTime"),logoutTime);
                        if(p1!=null){
                            p1=cb.and(p1,predicate);
                        }else{
                            p1=predicate;
                        }
                    }
                    return p1;
                }
                //跟拒上线时间进行排序 login_time  属性名称中不能有下划线
                ,new PageRequest(page-1,size,new Sort(Sort.Direction.DESC, "loginTime")));
        return list;
    }

    /**
     * 强制用户下线
     * @param
     * @return  boolean
     */
    @Override
    @Transactional
    public boolean forceUserOffline(SysUser user,OnLineLog onLineLog) {
        boolean flag=false;
        try {
            onLineLogRepository.save(onLineLog);//更新用户
            sysUserRepository.save(user); //更新用户
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }



    /**
     * 根据访问记录查询访问明细
     * @param onLineId 访问记录id
     * @return List<VDeviceVist>
     */
    @Override
    public OnLineLog findOneOnline(String onLineId) {
        OnLineLog onLineLog=onLineLogRepository.findOne(onLineId);
        return onLineLog;
    }

    @Override
    public OnLineLog findOneOnline(String sysUserId, int isToffine) {
        List<OnLineLog> onLineLogs= onLineLogRepository.findAllByUserIdAndIstOffline(sysUserId,isToffine);
        if(onLineLogs.size()>0){
            OnLineLog onLineLog=onLineLogs.get(0);
            return onLineLog;
        }else{
            return null;
        }
    }

    @Override
    public OnLineLog findOneOnline(SysUser sysUser, Timestamp loginOutTime) {
        OnLineLog onLineLog=onLineLogRepository.findByUserAndLogoutTime(sysUser,null);
        return onLineLog;
    }


    //登录时创建访问日志
    public OnLineLog loginon(String ip) {
        OnLineLog online = new OnLineLog();
        online.setLogoutTime(new Timestamp(System.currentTimeMillis()));//访问时间
        online.setLoginIp(ip);//登录ip
        //online.setUser();从session里面获取
        online.setVistEquipCount(0);//访问设备数
        online.setOnlineRate(0L);//访问流量
        onLineLogRepository.save(online);
        return online;
    }


    //用户开始访问设计记录接口 记录访问设备数
    @Override
    public void addvistEquipCount(String userid) {
        SysUser user = sysUserRepository.findOne(userid);
        OnLineLog online = onLineLogRepository.findByUserAndLogoutTime(user, null);
        online.setVistEquipCount(online.getVistEquipCount()+1);
        onLineLogRepository.save(online);
    }


    //用户下线更新数据
    @Override
    public OnLineLog loginout(String id,Long onlineRate) {
        OnLineLog online = onLineLogRepository.findOne(id);
        //下线时间
        online.setLogoutTime(new Timestamp(System.currentTimeMillis()));
        //现在流量
        online.setOnlineRate(onlineRate);
        return online;
    }

    //用户登录  添加登录日志
    @Override
    public OnLineLog addOnlineLog(OnLineLog onLineLog) {
         return  onLineLogRepository.save(onLineLog);
    }

    @Override
    public void updateLoginout(Timestamp loginoutTime,String id) {
         onLineLogRepository.updateLoginout(loginoutTime,id);
    }


}
