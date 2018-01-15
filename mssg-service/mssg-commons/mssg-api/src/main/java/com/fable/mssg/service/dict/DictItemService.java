package com.fable.mssg.service.dict;


import com.fable.mssg.domain.dictmanager.DictItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

/**
 * description  字典项接口
 * @author xiejk 2017/9/30
 */
public interface DictItemService {

    /**
     * 根据dictCode 查询
     * @param dictCode
     * @return
     */
    boolean findAllByDictCode(Long dictCode);


    /**
     * 分页查询
     * @param dictCode
     * @param dictItemName
     * @param dictItemCode
     * @param size
     * @param page
     * @return
     */
    Page<DictItem> findAllPageByDictCode(Long dictCode,String dictItemName,Long dictItemCode,int size,int page);

    /**
     * 根据字典项名称查询时候存在
     * @param dictItemName
     * @return
     */
    boolean findDictItemByDictItemName(String dictItemName);

    DictItem findByDictItemCode(Long itemCode);

    /**
     * 新增字典项
     * @param dictItem
     * @return
     */
    boolean insertDictItem(DictItem dictItem);

    /**
     * 根据id 查询
     * @param id
     * @return
     */
    DictItem findOneDictItem(long id);

    /**
     * 修改dictItem
     * @param dictItem
     * @return
     */
    boolean updateDictItem(DictItem dictItem);


    /**
     * 导入字典项
     * @param inputStream
     * @return
     */
    String leadIn(InputStream inputStream);

    /**
     * 导出字典项
     * @return
     */
    HSSFWorkbook leadOut();


    //查询设备厂商
    List<DictItem> findAll(Long dictCode);


    String getName(Integer code);
}
