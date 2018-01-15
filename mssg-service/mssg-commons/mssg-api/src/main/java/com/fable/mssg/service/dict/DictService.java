package com.fable.mssg.service.dict;


import com.fable.mssg.domain.dictmanager.Dict;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import java.io.InputStream;
import java.util.List;

/**
 * description  字典接口
 * @author xiejk 2017/9/30
 */
public interface DictService {

    /**
     * 查询全部的字典
     * @return
     */
     List<Dict> findAllDict();

    /**
     * 分页查询字典接口
     * @param dictName 字典名称
     * @param dictCode 字典代码
     * @param size 每页显示个数
     * @param page 页数
     * @return  Page<Dict>
     */
     Page<Dict> findAllPageDict(String dictName,String dictCode,int size,int page);


    /**
     * 新增字典
     * @param d 字典对象
     * @return Boolean
     */
    public Boolean insertDict(Dict d);

    /**
     * 按名称查询
     * @param dictName  字典名称
     * @return boolean  存在返回false
     */
    public boolean findDictByName(String dictName);

    /**
     * 按名称查询
     * @param dictCode  字典名称
     * @return boolean  存在返回false
     */
    public boolean findDictBydictCode(Long dictCode);


    /**
     * 删除字典信息
     * @param id
     * @return
     */
     boolean delDict(String id);

    /**
     * 查找单个字典
     * @param id
     * @return
     */
     Dict findOneDict(String id);

    /**
     * 导入字典
     * @param inputStream
     * @return
     */
    String leadIn(InputStream inputStream);

    /**
     * 导出字典
     * @return
     */
    HSSFWorkbook leadOut();

}
