package com.fable.mssg.user.repository;

import com.fable.mssg.domain.usermanager.SysMenu;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author: xiejk Created on 17:21 2017/11/1 0001
 */
public interface SysMenuRepository extends GenericJpaRepository<SysMenu, String>, JpaSpecificationExecutor<SysMenu> {

    /**
     * 根据菜单类型查找
     * @return
     */
    @Query(nativeQuery = true,value = "SELECT  menu.* from  mssg_menu  menu where MENU_TYPE=?1 ORDER BY MENU_PID ASC ")
   List<SysMenu> findByMenuType(int menuType);

    /**
     * 根据用户id查询用户菜单列表
     * @param
     * @return
     */
    @Query(nativeQuery = true, value = " select mme.* from mssg_role   mr  INNER JOIN mssg_role_menu  mm on mr.ID=mm.ROLE_ID  " +
            "INNER JOIN mssg_menu mme ON mme.ID=mm.MENU_ID  where mr.ID = ?1 and mme.MENU_TYPE=?2 ORDER BY mme.MENU_SEQ")
   List<SysMenu> querySysMenuByUserId(String roleId,int menuType);

}
