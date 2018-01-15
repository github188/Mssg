package com.fable.mssg.user.repository;

import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.domain.usermanager.SysUser;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * @author: yuhl Created on 17:15 2017/11/1 0001
 */
public interface SysRoleRepository extends GenericJpaRepository<SysRole, String> {

    /**
     * 根据id查询角色信息
     * @param id
     * @return
     */
    Set<SysRole> findById(String id);
}
