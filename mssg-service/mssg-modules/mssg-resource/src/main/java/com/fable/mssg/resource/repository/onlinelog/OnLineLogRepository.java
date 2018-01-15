package com.fable.mssg.resource.repository.onlinelog;


import com.fable.mssg.domain.OnLineLog;
import com.fable.mssg.domain.usermanager.SysUser;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * 用户访问记录dao层
 * @author  xiejk 2017/10/26
 */
public interface OnLineLogRepository extends GenericJpaRepository<OnLineLog,String> ,JpaSpecificationExecutor<OnLineLog> {

    /**
     * 根据user和下线时间查询登录在线日志
     * @return
     */
     OnLineLog findByUserAndLogoutTime(SysUser user, Timestamp loginout);


    List<OnLineLog> findByLogoutTimeIsNull();

    @Transactional
    @Modifying
    @Query("update OnLineLog onlinelog set onlinelog.logoutTime=?1 where onlinelog.id=?2" )
    void updateLoginout(Timestamp loginoutTime,String id);

    @Query("select onlinelog from OnLineLog  onlinelog  where onlinelog.user.id=?1 and onlinelog.istOffline=?2 order by onlinelog.logoutTime desc")
    List<OnLineLog> findAllByUserIdAndIstOffline(String userId,int isToffline );

}
