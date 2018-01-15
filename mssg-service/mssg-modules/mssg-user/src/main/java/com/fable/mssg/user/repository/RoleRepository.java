package com.fable.mssg.user.repository;


import com.fable.mssg.domain.usermanager.SysRole;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 用户角色dao层
 * @author  xiejk 2017/10/26
 */
public interface RoleRepository extends GenericJpaRepository<SysRole,String> {

     List<SysRole> findAllByRoleType(int roleType);

     @Query("select sysRole from SysRole sysRole where sysRole.roleType=?1 and sysRole.roleName<>?2 ")
     List<SysRole> findAllByRoleType(int roleType,String rolenName);

     List<SysRole> findByRoleCode(String roleCode);

     @Query("select sysRole from SysRole sysRole where sysRole.roleCode=?1 and sysRole.id<>?2 ")
     List<SysRole> findByRoleCode(String roleCode,String id);

     List<SysRole> findAllByRoleName(String roleName);

     @Query("select sysRole from SysRole sysRole where sysRole.roleName=?1 and sysRole.id<>?2 ")
     List<SysRole> findAllByRoleName(String roleName,String id);


}
