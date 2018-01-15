package com.fable.mssg.resource.converter;

import com.fable.mssg.domain.usermanager.SysRole;
import com.fable.mssg.resource.vo.VRole;
import com.slyak.spring.jpa.converter.RepoBasedConverter;
import org.springframework.stereotype.Service;

/**
 * 用户角色转换类
 * @author  xiejk 2017/9/30
 */
@Service
public class RoleConverter extends RepoBasedConverter<SysRole,VRole,String> {
    @Override
    protected VRole internalConvert(SysRole source) {
        return VRole.builder()
                //角色id
                .id(source.getId())
                //角色描述
                .role_decription(source.getRoleDecription())
                //角色名称
                .roleName(source.getRoleName())
                //角色code
                .roleCode(source.getRoleCode())
                .build();
    }

}
