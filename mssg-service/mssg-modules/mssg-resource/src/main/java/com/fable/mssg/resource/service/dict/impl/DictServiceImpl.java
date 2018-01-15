package com.fable.mssg.resource.service.dict.impl;

import com.fable.mssg.domain.dictmanager.Dict;
import com.fable.mssg.resource.repository.dict.DictRepository;
import com.fable.mssg.service.dict.DictService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * description  字典接口实现类
 * @author xiejk 2017/9/30
 */
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    DictRepository dictRepository;

    /**
     * 查询全部的字典接口
     * @return List<dict>
     */
    public List<Dict> findAllDict() {
        return dictRepository.findAll();
    }

    /**
     * 分页查询字典接口
     * @param dictName 字典名称
     * @param dictCode 字典代码
     * @param size 每页显示个数
     * @param page 页数
     * @return  Page<Dict>
     */
    @Override
    public Page<Dict> findAllPageDict(String dictName, String dictCode, int size, int page) {
        return dictRepository.findAll(
                (root, query, cb) -> {
                    Predicate p1=null;
                    //条件查询
                    if(dictName!=null&&dictName!=""){
                        p1 = cb.like(root.get("dictName"), "%" + dictName + "%");
                    }
                    if(dictCode!=null&&dictCode!=""){
                        Predicate p= cb.equal(root.get("dictCode"),  dictCode );
                        if(p1!=null){
                            p1=cb.and(p,p1);
                        }else{
                            p1=p;
                        }
                    }
                    return p1;
                }
                ,new PageRequest(page,size));
    }


    /**
     * 新增字典
     * @param d 字典对象
     * @return Boolean
     */
    @Override
    public Boolean insertDict(Dict d) {
        Boolean flag;
        try {
            flag=true;
            dictRepository.save(d);
        } catch (Exception e) {
            flag=false;
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 按名称查询
     * @param dictName  字典名称
     * @return boolean  存在返回false
     */
    @Override
    public boolean findDictByName(String dictName) {
        List<Dict> d=dictRepository.findByDictName(dictName);
        if(d.size()==0){
            return  true;
        }else{
            return false;
        }
    }


    /**
     * 根据dictCode查找
     * @param dictCode  字典名称
     * @return
     */
    @Override
    public boolean findDictBydictCode(Long dictCode) {
        return dictRepository.findByDictCode(dictCode).isEmpty();
    }



    /**
     * 删除字典信息
     * @param id 字典id
     * @return boolean
     */
    @Override
    public boolean delDict(String id) {
        boolean flag=false;
        try {
            dictRepository.delete(id);
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查找一个字典
     * @param id 字典id
     * @return Dict
     */
    @Override
    public Dict findOneDict(String id) {
        return dictRepository.findOne(id);
    }

    /**
     * 导入字典
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
            List<Dict> dicts = new ArrayList<>();
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                Dict dict = new Dict();
               String dictName=getString(row.getCell(0));
                if(dictName!=null){
                    if(dictRepository.findByDictName(dictName).isEmpty()){
                        dict.setDictName(dictName);
                    }else {
                        throw new RuntimeException("字典名称重复");
                    }
                }
                dict.setDictRemark(getString(row.getCell(1)));
                dict.setCreateTime(new Timestamp(System.currentTimeMillis()));
                //dict.setCreateUser();
                dicts.add(dict);
            }
            dictRepository.save(dicts);
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
     * 导出字典
     * @return
     */
    @Override
    public HSSFWorkbook leadOut() {
        List<Dict> dicts = dictRepository.findAll();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("dict");
        sheet.setDefaultColumnWidth(20);
        //设置标题
        HSSFRow head = sheet.createRow(0);
        head.createCell(0).setCellValue("字典ID");
        head.createCell(1).setCellValue("字典分类编码");
        head.createCell(2).setCellValue("字典分类名称");
        head.createCell(3).setCellValue("字典分类描述");
        int rowNum = 1;
        for (Dict dict : dicts) {
            HSSFRow row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(dict.getId());
            row.createCell(1).setCellValue(dict.getDictCode());
            row.createCell(2).setCellValue(dict.getDictName());
            row.createCell(3).setCellValue(dict.getDictRemark());
            rowNum++;
        }
        return workbook;
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
