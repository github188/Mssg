package com.fable.mssg.catalog.repository;

import com.fable.mssg.domain.equipment.EquipAttribute;
import com.slyak.spring.jpa.GenericJpaRepository;

/**
 * @Description
 * @Author wangmeng 2017/11/15
 */
public interface EquipAttributeRepository extends GenericJpaRepository<EquipAttribute,String>{
    EquipAttribute findBySbbm(String deviceId);
}
