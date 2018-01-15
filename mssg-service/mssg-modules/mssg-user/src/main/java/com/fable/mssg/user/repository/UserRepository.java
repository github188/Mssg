package com.fable.mssg.user.repository;

import com.fable.mssg.domain.usermanager.SysUser;
import com.slyak.spring.jpa.GenericJpaRepository;

public interface UserRepository extends GenericJpaRepository<SysUser,String> {
}