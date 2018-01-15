package com.fable.mssg.domain.usermanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author: yuhl Created on 13:42 2017/11/1 0001
 */
@Entity
@Table(name = "mssg_role_menu")
@Data
public class SysRoleMenu extends AbstractUUIDPersistable {

    @Column(name = "role_id")
    private String roleId; // 角色id

    @Column(name = "menu_id")
    private String menuId; // 菜单id

}
