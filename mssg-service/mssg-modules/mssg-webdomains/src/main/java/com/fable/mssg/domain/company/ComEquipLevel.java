package com.fable.mssg.domain.company;

import com.fable.framework.web.support.jpa.AbstractUUIDPersistable;
import com.fable.mssg.domain.dictmanager.DictItem;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description
 * @Author wangmeng 2017/11/16
 */
@Entity
@Table(name = "mssg_comlevel_equiplevel")
@Data
public class ComEquipLevel extends AbstractUUIDPersistable {

    public static final Integer ALL = -1;//最大权限,设备等级无关
    @Column(name = "COM_LEVEL")
    Integer comLevel;
    @Column(name = "EQUIP_LEVEL")
    Integer equipLevel;
    @Column(name = "REMARK", length = 2000)
    String remark;
}
