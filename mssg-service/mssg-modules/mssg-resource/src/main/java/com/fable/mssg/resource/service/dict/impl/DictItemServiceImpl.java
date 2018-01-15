package com.fable.mssg.resource.service.dict.impl;

import com.fable.mssg.domain.dictmanager.DictItem;
import com.fable.mssg.resource.repository.dict.DictItemRepository;
import com.fable.mssg.service.dict.DictItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description  字典项接口实现类
 * @author xiejk 2017/9/30
 */
@Service
@Slf4j
public class DictItemServiceImpl  implements DictItemService {

    @Autowired
    DictItemRepository dictItemRepository;

    Map<Long,String> dictionary;

    @PostConstruct
    public void getDictItem(){
        dictionary = new HashMap<>();
        List<DictItem> dictionaries = dictItemRepository.findAll();
        for(DictItem dictItem :dictionaries){
            dictionary.put(dictItem.getDictItemCode(),dictItem.getDictItemName());
        }
        log.info("加载{}条数据字典",dictionaries.size());

    }


    /**
     *根据dictCode查询时候有值
     * @param dictCode
     * @return boolean
     */
    @Override
    public boolean findAllByDictCode(Long dictCode) {
        if (!dictItemRepository.findAllByDictCode(dictCode).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }




    /**
     * 分页查询
     * @param dictCode
     * @param dictItemName
     * @param dictItemCode
     * @param size
     * @param page
     * @return
     */
    @Override
    public Page<DictItem> findAllPageByDictCode(Long dictCode,String dictItemName,Long dictItemCode,int size,int page) {
        return dictItemRepository.findAll(
                (root, query, cb) -> {
                    Predicate p1=cb.equal(root.get("dictCode"),dictCode);
                    //条件查询
                    if(dictItemName!=null&&dictItemName!=""){
                        Predicate p2 = cb.like(root.get("dictItemName"), "%" + dictItemName + "%");
                        p1=cb.and(p1,p2);
                    }
                    if(dictItemCode!=null){
                        Predicate p2= cb.equal(root.get("dictItemCode"),  dictItemCode );
                        p1=cb.and(p2,p1);
                    }
                    return p1;
                }
                ,new PageRequest(page,size));
    }

    /**
     * 根据字典项名称查询时候存在
     * @param dictItemName
     * @return
     */
    @Override
    public boolean findDictItemByDictItemName(String dictItemName) {
        if(dictItemRepository.findAllByDictItemName(dictItemName).isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public DictItem findByDictItemCode(Long itemCode) {
        return dictItemRepository.findByDictItemCode(itemCode);
    }

    /**
     * 新增字典项
     * @param dictItem
     * @return
     */
    @Override
    public boolean insertDictItem(DictItem dictItem) {
        try {
            dictItemRepository.save(dictItem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public DictItem findOneDictItem(long id) {
        return dictItemRepository.findOne(id);
    }

    /**
     * 修改dictItem
     * @param dictItem
     * @return
     */
    @Override
    public boolean updateDictItem(DictItem dictItem) {
       return insertDictItem(dictItem);
    }

    /**
     * 导入字典项
     * @param inputStream
     * @return
     */
    @Override
    @Transactional
    public String leadIn(InputStream inputStream) {
        String message;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            List<DictItem> dicts = new ArrayList<>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                DictItem dict = new DictItem();
                String dictItemName=getString(row.getCell(0));
                if(dictItemName!=null){
                    if(findDictItemByDictItemName(dictItemName)){
                        dict.setDictItemName(dictItemName);
                    }else {
                        throw new RuntimeException("字典项名称重复");
                    }
                }
                Long dictCode=getLong(row.getCell(1));
                if(dictCode!=null){
                    if(!findAllByDictCode(dictCode)){
                        dict.setDictCode(dictCode);
                    }else{
                        throw new RuntimeException("字典编码不存在");
                    }
                }
                dict.setDictItemRemark(getString(row.getCell(2)));
                dict.setCreateTime(new Timestamp(System.currentTimeMillis()));
                //dict.setCreateUser();
                dicts.add(dict);
            }
            dictItemRepository.save(dicts);
            message="导入成功";
        } catch (IOException e) {
            e.printStackTrace();
            message=e.getMessage();
        } catch (Exception e) {
            message="名称，编码重复";
            e.printStackTrace();
        }
        return  message;
    }


    /**
     * 导出字典项
     * @return
     */
    @Override
    public HSSFWorkbook leadOut() {
        List<DictItem> dicts = dictItemRepository.findAll();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("dictItem");
        sheet.setDefaultColumnWidth(20);
        //设置标题
        HSSFRow head = sheet.createRow(0);
        head.createCell(0).setCellValue("字典项编码");
        head.createCell(1).setCellValue("字典项ID");
        head.createCell(2).setCellValue("字典分类编码");
        head.createCell(3).setCellValue("字典项分类名称");
        head.createCell(4).setCellValue("字典项分类描述");
        int rowNum = 1;
        for (DictItem dict : dicts) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(dict.getDictItemName());
            row.createCell(1).setCellValue("");
            row.createCell(2).setCellValue(dict.getDictCode());
            row.createCell(3).setCellValue(dict.getDictItemName());
            row.createCell(4).setCellValue(dict.getDictItemRemark());
            rowNum++;
        }
        return workbook;
    }


    //查询字典项信息
    @Override
    public List<DictItem> findAll(Long dictCode) {
        return dictItemRepository.findAllByDictCode(dictCode);
    }

    @Override
    public String getName(Integer code) {
        return this.dictionary.get(code.longValue());
    }

    //从cell中读取int
    private Long getLong(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            return (long) cell.getNumericCellValue();
        }
    }

    //从cell中读取String
    private String getString(HSSFCell cell) {
        if (cell == null) {
            return null;
        } else {
            return cell.getStringCellValue();

        }

    }
}
