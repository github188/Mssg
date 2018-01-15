package com.fable.mssg.company.repository;

import com.fable.mssg.domain.company.ComEquipLevel;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description
 * @Author wangmeng 2017/11/16
 */
public interface ComEquipLevelRepository extends GenericJpaRepository<ComEquipLevel,String> {
    List<ComEquipLevel> findByComLevel(Integer comLevel);

    @Query(value = "SELECT distinct ce.comLevel FROM ComEquipLevel ce ")
    List<Integer> findAllComLevelCode();
}
