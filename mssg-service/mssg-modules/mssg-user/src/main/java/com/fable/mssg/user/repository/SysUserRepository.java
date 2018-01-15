package com.fable.mssg.user.repository;


import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.domain.company.Company;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * 用户dao层
 * @author  xiejk 2017/10/26
 */
public interface SysUserRepository extends GenericJpaRepository<SysUser,String> ,JpaSpecificationExecutor<SysUser> {
    @Query(nativeQuery = true,
            value = "SELECT * FROM (SELECT msu.USER_NAME , mc.NAME as COMPANY_NAME, " +
                    "COUNT(IF(mbl.OP_TYPE = 1,TRUE,NULL)) as REALTIME, " +
                    "COUNT(IF(mbl.OP_TYPE = 2,TRUE,NULL)) as HISTORY " +
                    "FROM mssg_sys_user msu " +
                    "JOIN mssg_bus_log mbl ON msu.ID = mbl.VISIT_USER " +
                    "JOIN mssg_company mc ON mc.ID = msu.COM_ID " +
                    "WHERE mbl.START_TIME > ?1 "+
                    "GROUP BY msu.USER_NAME " +
                    "LIMIT ?2) a "+
                    "ORDER BY (a.REALTIME+a.HISTORY) DESC")
    List findTopViewUser(Timestamp timestamp,Integer size);

    List<SysUser> findByLoginState(Integer state);

    @Query(nativeQuery = true,
            value = "SELECT * FROM (  " +
                    " SELECT msu.USER_NAME , mc.NAME as COMPANY_NAME,   " +
                    " COUNT(IF(mbl.OP_TYPE = 1,TRUE,NULL)) as REALTIME,   " +
                    " COUNT(IF(mbl.OP_TYPE = 2,TRUE,NULL)) as HISTORY, " +
                    " COUNT(DISTINCT mbl.DS_ID) as VIEW_DEVICE "+
                    " FROM mssg_sys_user msu   " +
                    " JOIN mssg_bus_log mbl ON msu.ID = mbl.VISIT_USER   " +
                    " JOIN mssg_company mc ON mc.ID = msu.COM_ID   " +
                    " WHERE msu.COM_ID = ?2  " +
                    " AND mbl.START_TIME > ?1 "+
                    " GROUP BY msu.USER_NAME   " +
                    " LIMIT ?3 "+
                    ") a  " +
                    "ORDER BY (a.REALTIME+a.HISTORY) DESC")
    List findTopViewUserByComId(Timestamp timestamp,String comId,Integer size);


    /**
     * 根据登录名查询用户信息
     * @param loginName
     * @return
     */
    @Query("select user from SysUser user where loginName = ?1 and  user.deleteFlag=0 and user.state=0")
    SysUser findByUserName(@Param("loginName")String loginName);


    /**
     * 通过公司 跟删除标识
     * @param company
     * @param i
     * @return
     */
    List<SysUser> findByComIdAndDeleteFlag(Company company, int i);

    /**
     * 通过身份证号查询用户信息
     * @param idCard
     * @return
     */
    SysUser findByIdCard(String idCard);

    /**
     * 根据sipId查询用户信息
     * @param sipId
     * @return
     */
    SysUser findBySipId(String sipId);

    /**
     * 更改用户登录状态
     * @param
     */
    @Transactional
    @Modifying
    @Query(value = "update SysUser user set user.loginState=0  where user.id=?1")
    void  updateUserLogout(String id);

    @Transactional
    @Modifying
    @Query(value = "update SysUser user set user.loginState=1  where user.id=?1")
    void  updateUserLogined(String id);

    List<SysUser> findAllByRoleId(SysRole roleId);

    @Query(value = "SELECT user FROM SysUser user WHERE user.roleId.roleCode='10000005'")
    List<SysUser> findApproval();


}
