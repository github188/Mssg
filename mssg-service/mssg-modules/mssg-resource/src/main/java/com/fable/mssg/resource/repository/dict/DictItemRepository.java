package com.fable.mssg.resource.repository.dict;


import com.fable.mssg.domain.dictmanager.DictItem;
import com.slyak.spring.jpa.GenericJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 字典dao层
 * @author  xiejk 2017/9/30
 */
public interface DictItemRepository extends GenericJpaRepository<DictItem,Long>,JpaSpecificationExecutor<DictItem> {

    /**
     * 根据字典code查询
     * @param dictCode 字典code
     * @return  List<DictItem>
     */
     List<DictItem> findAllByDictCode(Long dictCode);

    /**
     * 根据字典项名称查询
     * @param dictItemName
     * @return
     */
    List<DictItem> findAllByDictItemName(String dictItemName);

    DictItem findByDictItemCode(Long dictItemCode);





}
