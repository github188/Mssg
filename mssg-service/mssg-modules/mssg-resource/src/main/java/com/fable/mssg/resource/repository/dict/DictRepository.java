package com.fable.mssg.resource.repository.dict;


import com.fable.mssg.domain.dictmanager.Dict;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 字典dao层
 * @author  xiejk 2017/9/30
 */
public interface DictRepository extends GenericJpaRepository<Dict,String> ,JpaSpecificationExecutor<Dict> {

    /**
     * 根据名称查询
     * @param dictName
     * @return
     */
     List<Dict> findByDictName(String dictName);

    /**
     * 根据dictCode 查询
     * @param dictCode
     * @return
     */
     List<Dict> findByDictCode(Long dictCode);





}
