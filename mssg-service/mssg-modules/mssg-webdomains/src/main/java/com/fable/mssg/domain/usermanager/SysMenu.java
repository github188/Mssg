package com.fable.mssg.domain.usermanager;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author: yuhl Created on 13:35 2017/11/1 0001
 */
@Entity
@Table(name = "mssg_menu")
@Data
public class SysMenu extends AbstractUUIDPersistable implements Serializable{

    @Column(name = "menu_name")
    private String label; // 菜单名称

    @Column(name = "menu_code")
    private String menuCode; // 菜单编码

    @Column(name = "menu_link")
    private String url; // 菜单url

    @Column(name = "menu_decription")
    private String menuDescription; // 菜单描述

    @Column(name = "menu_type")
    private  int menuType;//菜单类型

    @Column(name = "MENU_PID")
    private  String pid;//父级id

    @Column(name="MENU_ICON")
    private  String icon;

    @Column(name = "MENU_LEVEL")
    private String menuLevel; // 菜单层级

    @Column(name = "MENU_SEQ")
    private String menuSeq; // 菜单序号
}
