package com.fable.mssg.user.repository;

import com.fable.mssg.domain.usermanager.SysRoleMenu;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author: yuhl Created on 17:21 2017/11/1 0001
 */
public interface SysMenuRoleRepository extends GenericJpaRepository<SysRoleMenu, String> {

    /**
     * 根据角色id查询菜单列表
     * @param roleId
     * @return
     */
    @Query("select menu from SysRoleMenu menu where menu.roleId = ?1")
    List<SysRoleMenu> findByRoleId(@Param("roleId") String roleId);

    void deleteByRoleId(String roleId);

}
