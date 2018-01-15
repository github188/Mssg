package com.fable.mssg.service.user;


import com.fable.mssg.domain.usermanager.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

/**
 * description  用户接口
 * @author xiejk 2017/10/26
 */
public interface SysUserService {

    /**
     * 分页查询所有的用户信息
     * @param size 每页显示个数
     * @param page 当前页数
     * @param userName 用户名称
     * @param loginUser 用户登录名称
     * @param roleName 用户角色名称
     * @return List<VSysUser>
     */
    Page<SysUser> findPageUserByCondition(int size, int page, String userName, String loginUser
                                            , String roleName,String comId);

    /**
     *  根据用户名称查找是否已经存在
     * @param loginName  用户登录名
     * @return  boolean
     */
    boolean findUserByUserName(String loginName);



    boolean findUserByUserName(String loginName,String id);

    /**
     * 根据身份证账号查用户是否已经存在
     * @param IDCard 身份证号
     * @return boolean
     */
    boolean findUserByUserIDCard(String IDCard);

     boolean findUserByUserIDCard(String IDCard,String id);

    /**
     * 新增用户
     * @param sysUser  用户对象
     * @return 是否新增成功
     */
    SysUser insertSysUser(SysUser sysUser);

    /**
     * 删除用户
     * @param uid  用户id
     * @return 是否删除成功
     */
    boolean delSysUser(String uid);

    /**
     * 编辑用户
     * @param sysUser  用户对象
     * @return  boolean
     */
    boolean  updateSysUser(SysUser sysUser);

    /**
     * 启用用户
     * @param userId  用户id
     * @return String
     */
    boolean forceUserEnabled(String userId);

    /**
     * 查找单个的用户
     * @param uid  用户id
     * @return SysUser
     */
    SysUser findOneUserByUserId(String uid);

    /**
     * 根据身份证号查询用户信息
     * @param idCard
     * @return
     */
    SysUser findByIdCard(String idCard);

    /**
     * 根据用户登录名查询用户信息
     * @param loginName
     * @return
     */
    SysUser findByLoginName(String loginName);

    Page<SysUser> findUserByCompany( String comId, String loginName, String userName, String roleid,int size,int page);

    long count(SysUser sysUser);

    List findTopViewUser(Timestamp endTime, Integer size);

    List findTopViewUserByComId(Timestamp endTime, String comId, Integer size);

   /* *//**
     * 导入用户
     * @param
     * @return
     *//*
    List<SysUser>  leadIn( MultipartFile userfile);*/

    /**
     * 根据sipId查询用户信息
     * @param sipId
     * @return
     */
    SysUser findBySipId(String sipId);


    //保存用户未登录
    void updateLoginState(String id);

    //保存用户已登录
    void updateLogin(String id);

    List<SysUser> findAllApprovalUser();

    List<SysUser> findAllUserByRoleId(String roleId);
}
