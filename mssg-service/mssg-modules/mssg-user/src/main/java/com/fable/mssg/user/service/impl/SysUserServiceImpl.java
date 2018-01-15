package com.fable.mssg.user.service.impl;

import com.fable.framework.core.support.remoting.Exporter;
import com.fable.mssg.bean.info.LoginUserInfo;
import com.fable.mssg.domain.company.Company;
import com.fable.mssg.domain.dictmanager.Dict;
import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import com.fable.mssg.service.user.exception.SysUserException;
import com.fable.mssg.user.repository.SysUserRepository;
import com.fable.mssg.service.user.SysUserService;
import com.fable.mssg.user.vo.SysUserDelFlag;
import com.fable.mssg.user.vo.SysUserStatus;
import com.fable.mssg.utils.MD5Utils;
import com.fable.mssg.utils.login.LoginUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * description 用户接口实现类
 *
 * @author xiejk 2017/10/26
 */

@Service
@Exporter(interfaces = SysUserService.class)
public class SysUserServiceImpl implements SysUserService {

    //用户dao层操作对象
    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    EntityManagerFactory emf;

    /**
     * 分页查询用户信息
     *
     * @param size      每页显示个数
     * @param page      当前页数
     * @param userName  用户名称
     * @param loginName 用户登录名称
     * @param roleId    用户角色名称
     * @return List<VSysUser>
     */
    @Override
    public Page<SysUser> findPageUserByCondition(int size, int page, String userName, String loginName, String roleId
            , String comId) {
        //方法是生成查询条件的 在criteria 查询中，查询条件通过Predicate或Expression实例应用到CriteriaQuery对象上
        return sysUserRepository.findAll(
                (root, query, cb) -> {
                    Predicate p1 = cb.equal(root.get("deleteFlag"),0);//没有被删除的
                    Predicate p2 = cb.equal(root.get("state"),0);
                    Predicate p3 = cb.equal(root.get("state"),1);
                    Predicate p4 = cb.or(p2,p3);
                    p1=cb.and(p1,p4);
                    //userName 加上模糊查询条件
                    if (userName != null && !userName.equals("")) {
                        Predicate   predicate= cb.like(root.get("userName"), "%" + userName + "%");
                        p1=cb.and(p1,predicate);
                    }

                    //loginUser 加上模糊查询条件
                    if (loginName != null && !loginName.equals("")) {
                        Predicate predicate = cb.like(root.get("loginName"), "%" + loginName + "%");
                        p1 = cb.and(p1, predicate);
                    }
                    //roleName 加上用户角色的条件
                    if (roleId != null && !roleId.equals("")) {
                        Predicate predicate = cb.equal(root.get("roleId").get("id"), roleId);
                        p1 = cb.and(p1, predicate);
                    }
                    //comId 加上用户角色的条件
                    if (comId != null && !comId.equals("")) {
                        Predicate predicate = cb.equal(root.get("comId").get("id"), comId);
                        p1 = cb.and(p1, predicate);
                    }
                    return p1;
                }, new PageRequest(page, size,new Sort(Sort.Direction.DESC,"createTime"))
        );
    }

    /**
     * 根据用户名称查找是否已经存在  如果存在返回false  不存在  返回true
     * @param loginName 用户登录名
     * @return boolean
     */
    @Override
    public boolean findUserByUserName(String loginName) {
        List<com.fable.mssg.domain.usermanager.SysUser> list = sysUserRepository.findAll(
                (Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                    Predicate p1 = cb.equal(root.get("loginName"), loginName);
                    Predicate p2 = cb.equal(root.get("deleteFlag"), 0);
                    Predicate p3=cb.equal(root.get("state"), 0);//用户被拒绝
                    Predicate p4=cb.equal(root.get("state"), 1);//用户被拒绝
                    Predicate p5=cb.equal(root.get("state"), 2);//用户被拒绝
                    p3=cb.or(p3,p4,p5);
                    p1=cb.and(p1,p2,p3);
                    return p1;
                }

        );
        return list.size() == 0;
    }



    /**
     * 根据身份证账号查用户是否已经存在
     *
     * @param IDCard 身份证号
     * @return boolean
     */
    @Override
    public boolean findUserByUserIDCard(String IDCard) {
        List<SysUser> list = sysUserRepository.findAll(
                (Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                    Predicate p1 = cb.equal(root.get("idCard"), IDCard);
                    Predicate p2 = cb.equal(root.get("deleteFlag"), 0);
                    Predicate p3=cb.equal(root.get("state"), 0);//用户被拒绝
                    Predicate p4=cb.equal(root.get("state"), 1);//用户被拒绝
                    Predicate p5=cb.equal(root.get("state"), 2);//用户被拒绝
                    p3=cb.or(p3,p4,p5);
                    p1=cb.and(p1,p2,p3);
                    return p1;
                }
        );
        return list.size() == 0;
    }

    //说明存在id不相同但是登录名相同
    @Override
    public boolean findUserByUserName(String loginName,String id) {
        List<SysUser> list = sysUserRepository.findAll(
                (root, query, cb) -> {
                    Predicate p1 = cb.equal(root.get("loginName"), loginName);
                    Predicate p2 = cb.notEqual(root.get("id"), id);
                    Predicate p3 = cb.equal(root.get("deleteFlag"), 0);
                    p1 = cb.and(p1,p2,p3);
                    return p1;
                }
        );
          return  list.size()==0;
    }

    //说明存在id不相同但是身份证相同
    @Override
    public boolean findUserByUserIDCard(String IDCard,String id) {
        List<SysUser> list = sysUserRepository.findAll(
                (root, query, cb) -> {
                    Predicate p1 = cb.equal(root.get("idCard"), IDCard);
                    Predicate p2 = cb.notEqual(root.get("id"), id);
                    Predicate p3 = cb.equal(root.get("deleteFlag"), 0);
                    p1 = cb.and(p1,p2,p3);
                    return p1;
                }
        );
        return  list.size()==0;
    }

    /**
     * 新增用户
     *
     * @param sysUser 用户对象
     * @return boolean
     */
    @Override
    @Transactional
    public SysUser insertSysUser(SysUser sysUser) {
        SysUser user;
        if (!this.findUserByUserName(sysUser.getLoginName())) {
            throw new SysUserException(SysUserException.USER_LOGINNAME_ALREADY_EXIST);
        }
        if (!this.findUserByUserIDCard(sysUser.getIdCard())) {
            throw new SysUserException(SysUserException.USER_IDCARD_EXIST);
        }
        try {
            user=sysUserRepository.save(sysUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new SysUserException(SysUserException.USER_INSERT_ERROR);
        }

        return user;
    }

    @Override
    public Page<SysUser> findUserByCompany( String comId, String loginName, String userName, String roleId,int size,int page) {
        Company company = new Company();
        company.setId(comId);
        return sysUserRepository.findAll(
                (root, query, cb) -> {
                    Predicate p1 = cb.equal(root.get("deleteFlag"),0);//没有被删除的
                    Predicate p2 = cb.equal(root.get("comId"),company);//公司id
                    Predicate p6=cb.equal(root.get("roleId").get("roleType"),2);//共享端的
                    Predicate p3 = cb.equal(root.get("state"),0);
                    Predicate p4 = cb.equal(root.get("state"),1);
                    Predicate p5 = cb.or(p4,p3);
                    p1=cb.and(p1,p2,p6,p5);
                    //userName 加上模糊查询条件
                    if (userName != null && !userName.equals("")) {
                        Predicate  predicate = cb.like(root.get("userName"), "%" + userName + "%");
                        p1=cb.and(p1,predicate);
                    }
                    //loginUser 加上模糊查询条件
                    if (loginName != null && !loginName.equals("")) {
                        Predicate predicate = cb.like(root.get("loginName"), "%" + loginName + "%");
                        p1 = cb.and(p1, predicate);
                    }
                    //roleName 加上用户角色的条件
                    if (roleId != null && !roleId.equals("")) {
                        Predicate predicate = cb.equal(root.get("roleId").get("id"), roleId);
                        p1 = cb.and(p1, predicate);
                    }
                    return p1;
                },new PageRequest(page, size)
        );
    }

    @Override
    public long count(SysUser sysUser) {
        return sysUserRepository.count(Example.of(sysUser));
    }

    @Override
    public List findTopViewUser(Timestamp endTime, Integer size) {
        return sysUserRepository.findTopViewUser(endTime,size);
    }

    @Override
    public List findTopViewUserByComId(Timestamp endTime, String comId, Integer size) {
        return sysUserRepository.findTopViewUserByComId(endTime,comId,size);
    }

    //导入用户功能
   /* @Transactional
    @Override
    public List<SysUser> leadIn(MultipartFile userfile) {
       *//* List<SysUser> sysUsers = new ArrayList<>();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(userfile.getInputStream());
            HSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                SysUser sysUser = new SysUser();
                sysUser.setCreateTime(new Timestamp(System.currentTimeMillis()));//创建时间
                //sysUser.setCreateUser(loginUserInfo.getSysUser().getId());//创建人
                //sysUser.setComId(loginUserInfo.getSysUser().getComId());//设置所在的公司
                sysUser.setUserName(getString(row.getCell(0)));//设置用户名称
                sysUser.setCellPhoneNumber(getString(row.getCell(1)));//手机号码
                sysUser.setPassword(MD5Utils.getMD5Value(getString(row.getCell(2))));//用户初始密码
                sysUser.setIdCard(getString(row.getCell(3)));
                sysUser.setLoginName(getString(row.getCell(4)));
                sysUser.setTelphone(getString(row.getCell(5)));//办公电话
                sysUser.setDescription(getString(row.getCell(6)));//描述
                sysUser.setPosition(getString(row.getCell(7)));//职务
                SysRole role=new SysRole("d321f55f5f9542cbb272ac01f28cd0a7");
                sysUser.setRoleId(role);//设置角色  只有一个角色  单位访问员
                sysUser.setSalt("11");//设置加密方式
                sysUser.setState(0);//正常状态
                sysUser.setDeleteFlag(0);//未删除
                sysUser.setLoginState(0);//未登录
                sysUsers.add(sysUser);*//*
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sysUsers;
    }*/

    /**
     * 删除用户的方法
     *
     * @param uid 用户id
     * @return 是否删除成功
     */
    @Override
    @Transactional
    public boolean delSysUser(String uid) {
        boolean result = false;
        try {
            //更新用户的删除标识
            SysUser sysUser = sysUserRepository.findOne(uid);
            if (sysUser != null) {
                sysUser.setDeleteFlag(SysUserDelFlag.DELETED);
                //更新
                sysUserRepository.save(sysUser);
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 编辑用户
     *
     * @param sysUser 用户对象
     * @return boolean
     */
    @Override
    @Transactional
    public boolean updateSysUser(SysUser sysUser) {
        boolean flag=false;
        try {
            sysUserRepository.save(sysUser);
            flag= true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 启用用户
     * @param userId 用户id
     * @return boolean
     */
    @Override
    public boolean forceUserEnabled(String userId) {
        SysUser sysUser = sysUserRepository.findOne(userId);
        if (sysUser.getState() == SysUserStatus.DISABLED) {
            sysUser.setState(SysUserStatus.ENABLE);
            try {
                sysUserRepository.saveAndFlush(sysUser);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            throw new SysUserException(SysUserException.USER_ENABLED);
        }
    }

    /**
     * 查找单个的用户
     *
     * @param uid 用户id
     * @return SysUser
     */
    @Override
    public SysUser findOneUserByUserId(String uid) {
        return sysUserRepository.findOne(uid);
    }

    /**
     * 根据身份证号查询用户信息
     *
     * @param idCard
     * @return
     */
    @Override
    public SysUser findByIdCard(String idCard) {
        return sysUserRepository.findByIdCard(idCard);
    }

    /**
     * 根据用户登录名查询用户信息
     *
     * @param loginName
     * @return
     */
    @Override
    public SysUser findByLoginName(String loginName) {
        SysUser sysUser = sysUserRepository.findByUserName(loginName);
        return sysUser;
    }

    //从cell中读取int
    private Long getLong(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            return (long) cell.getNumericCellValue();
        }
    }

    /**
     * 根据sipId查询用户信息
     *
     * @param sipId
     * @return
     */
    @Override
    public SysUser findBySipId(String sipId) {
        return sysUserRepository.findBySipId(sipId);
    }




    @Override
    public void updateLoginState(String id) {
        sysUserRepository.updateUserLogout(id);
    }


    @Override
    public void updateLogin(String id) {
        sysUserRepository.updateUserLogined(id);
    }

    @Override
    public List<SysUser> findAllApprovalUser() {
        return sysUserRepository.findApproval();
    }

    //根据roleid 查询全部的用户
    @Override
    public List<SysUser> findAllUserByRoleId(String roleId) {
        SysRole sysRole=new SysRole(roleId);
        return  sysUserRepository.findAllByRoleId(sysRole);
    }
}
