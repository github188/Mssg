package com.fable.mssg.resource.web.dict;

import com.fable.mssg.domain.dictmanager.DictItem;
import com.fable.mssg.resource.converter.dictConverter.DictItemConverter;
import com.fable.mssg.service.dict.DictItemService;
import com.fable.mssg.service.dict.DictService;
import com.fable.mssg.utils.login.LoginUtils;
import com.fable.mssg.vo.dict.VDict;
import com.fable.mssg.vo.dict.VDictItem;
import com.fable.mssg.vo.DataTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

/**
 * description  字典项控制器
 * @author xiejk 2017/11/06
 */
@RestController
@RequestMapping("/dictItem")
@Api(value = "字典项控制器",description = "字典项控制器")
@Slf4j
@Secured(LoginUtils.ROLE_USER)
public class DictItemController {

    @Autowired
    DictItemService dictItemService;
    @Autowired
    DictItemConverter dictItemConverter;

    @Autowired
    DictService dictService;

    /**
     * 分页查询
     * @param size
     * @param page
     * @param dictItemName
     * @param dictItemCode
     * @param dictCode
     * @return DataTable
     */
    @ApiOperation(value = "分页查询",notes="分页查询")
    @RequestMapping(value = "findAllPageByDictCode",method = RequestMethod.GET)
    public DataTable findAllPageByDictCode(@RequestParam int size, @RequestParam int page, String dictItemName,
                                           Long dictItemCode, @RequestParam Long dictCode){
        Page<VDictItem> list=dictItemService.findAllPageByDictCode(dictCode,dictItemName,dictItemCode,size,page)
                .map(dictItemConverter);
        return DataTable.buildDataTable(list);
    }

    /**
     * 新增字典项
     * @param dictItemName
     * @param dictItemRemark
     * @param dictCode
     * @return
     */
    @ApiOperation(value = "新增字典项",notes="新增字典项")
    @RequestMapping(value = "insertDictItem",method = RequestMethod.POST)
    public String insertDictItem(@RequestParam String dictItemName, @RequestParam String  dictItemRemark
            ,@RequestParam Long dictCode){
        String message;
        if(dictItemService.findDictItemByDictItemName(dictItemName)){
            DictItem di=new DictItem();
            di.setCreateTime(new Timestamp(System.currentTimeMillis()));
            //di.setCreateUser(); 创建人
            di.setDictCode(dictCode);
            di.setDictItemRemark(dictItemRemark);
            di.setDictItemName(dictItemName);
            if(dictItemService.insertDictItem(di)){
                message="保存成功";
            }else {
                message="保存失败";
            }
        }else{
            message="字典项名称已经存在";
        }
        return  message;
    }

    //删除字典选项
    public String delDictItem(String ditemid){
        return "";
    }

    /**
     * 查询单个字典项
     * @param id
     * @return
     */
    @ApiOperation(value = "查询单个字典项",notes="查询单个字典项")
    @RequestMapping(value = "findOneDictItem",method = RequestMethod.GET)
    public DictItem findOneDictItem(Long id){
        return dictItemService.findOneDictItem(id);
    }

    /**
     * 修改字典项
     * @param id
     * @param dictCode
     * @param dictItemName
     * @param dictItemRemark
     * @return
     */
    @ApiOperation(value = "修改字典项",notes="修改字典项")
    @RequestMapping(value = "updateDictItem",method = RequestMethod.POST)
    public String updateDictItem(@RequestParam Long id,@RequestParam Long dictCode
                                ,@RequestParam String dictItemName, String dictItemRemark){
        String message;
        DictItem dictItem=dictItemService.findOneDictItem(id);
        if(dictService.findDictBydictCode(dictCode)){
            message="字典编码不存在，不能修改";
        }else{
                if(dictItemService.findDictItemByDictItemName(dictItemName)){
                    dictItem.setDictItemName(dictItemName);
                    dictItem.setDictItemRemark(dictItemRemark);
                    dictItem.setDictCode(dictCode);
                    //dictItem.setUpdateUser();
                    dictItem.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    if( dictItemService.updateDictItem(dictItem)){
                        message="修改字典项成功";
                    }else{
                        message="修改字典项失败";
                    }
                }else{
                    message="字典项名称已经存在，不能修改";
                }
        }
        return message;
    }


    /**
     * 导入字典的功能  只能导入2003版的表格
     * @param dictItemfile
     * @return
     */
    @ApiOperation(value = "导入字典项",notes = "导入字典项")
    @RequestMapping(value = "leadIn",method = RequestMethod.POST)
    public String leadIn(@RequestParam MultipartFile dictItemfile){
        String message;
        if(!dictItemfile.isEmpty()){
            try {
                message=dictItemService.leadIn(dictItemfile.getInputStream());
            } catch (Exception e) {
                throw  new RuntimeException("导入异常",e);
            }
        }else{
            message="文件是空的！！";
        }
        return  message;
    }

    /**
     * 导出字典项
     * @param res
     */
    @ApiOperation(value = "导出字典项",notes = "导出字典项")
    @RequestMapping(value = "/leadout", method = RequestMethod.GET)
    public void leadOut(HttpServletResponse res) {
        HSSFWorkbook workbook = dictItemService.leadOut();
        OutputStream out = null;
        try {
            res.setContentType("application/force-download");// 设置强制下载打开
            res.addHeader("Content-Disposition", "attachment;fileName=" + "dictItem.xls");// 设置文件名
            out = res.getOutputStream();
            workbook.write(out);
        } catch (IOException e) {
            log.error("导出异常{}", e);
            e.getMessage();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                log.error("导出异常{}", e);
            }
        }
    }

//    @ExceptionHandler(Exception.class)
//    public String handle(Exception e) {
//        log.error("单位异常{}", e);
//        return e.getMessage();
//    }

    @ApiOperation(value = "查询所有行业分类",notes = "字典")
    @RequestMapping(value = "/findAllIndustry", method = RequestMethod.GET)
    public List<VDictItem> findAllIndustry(){
        return dictItemConverter.convert(dictItemService.findAll(10000003L));
    }

    @ApiOperation(value = "查询所有主题分类",notes = "字典")
    @RequestMapping(value = "/findAllMain", method = RequestMethod.GET)
    public List<VDictItem> findAllMain(){
        return dictItemConverter.convert(dictItemService.findAll(10000002L));
    }


    //查询全部的设备厂商
    @ApiOperation(value = "查询全部的设备厂商",notes = "查询全部的设备厂商")
    @RequestMapping(value = "/findAllsbcs", method = RequestMethod.GET)
    public List<DictItem> findAllsbcs(){
        return dictItemService.findAll(3L);//
    }

    //查询全部的行政区域信息
    @ApiOperation(value = "查询全部的行政区域信息",notes = "查询全部的行政区域信息")
    @RequestMapping(value = "/findAllxzqu", method = RequestMethod.GET)
    public List<DictItem> findAllxzqu(){
        return dictItemService.findAll(10000005L);
    }


    //查询监控点位置
    @ApiOperation(value = "查询监控点位置",notes = "查询监控点位置")
    @RequestMapping(value = "/findAlljkdwz", method = RequestMethod.GET)
    public List<DictItem> findAlljkdwz(){
        return dictItemService.findAll(5L);
    }

    //查询全部的摄像机类型
    @ApiOperation(value = "查询全部的摄像机类型",notes = "查询全部的摄像机类型")
    @RequestMapping(value = "/findAllsxjlx", method = RequestMethod.GET)
    public  List<DictItem> findAllsxjlx(){
        return dictItemService.findAll(6L);
    }

    //查询全部的摄像机功能类型
    @ApiOperation(value = "查询全部的摄像机类型",notes = "查询全部的摄像机类型")
    @RequestMapping(value = "/findAllsxjgnlx", method = RequestMethod.GET)
    public  List<DictItem> findAllsxjgnlx(){
        return dictItemService.findAll(7L);
    }

    //查询补光属性
    @ApiOperation(value = "查询补光属性",notes = "查询补光属性")
    @RequestMapping(value = "/findAllbgsx", method = RequestMethod.GET)
    public  List<DictItem> findAllbgsx(){
        return dictItemService.findAll(8L);
    }

    //摄像机编码格式
    @ApiOperation(value = "摄像机编码格式",notes = "摄像机编码格式")
    @RequestMapping(value = "/findAllsxjbmge", method = RequestMethod.GET)
    public  List<DictItem> findAllsxjbmge(){
        return dictItemService.findAll(9L);
    }

    //摄像机位置类型
    @ApiOperation(value = "摄像机编码格式",notes = "摄像机编码格式")
    @RequestMapping(value = "/findAllsxjwzlx", method = RequestMethod.GET)
    public  List<DictItem> findAllsxjwzlx(){
        return dictItemService.findAll(10L);
    }

    //监控方位
    @ApiOperation(value = "监控方位",notes = "监控方位")
    @RequestMapping(value = "/findAlljkfw", method = RequestMethod.GET)
    public  List<DictItem> findAlljkfw(){
        return dictItemService.findAll(11L);
    }

    //维修状态
    @ApiOperation(value = "维修状态",notes = "维修状态")
    @RequestMapping(value = "/findAllwxzt", method = RequestMethod.GET)
    public  List<DictItem> findAllwxzt(){
        return dictItemService.findAll(12L);
    }

    //所属部门和行业
    //维修状态
    @ApiOperation(value = "维修状态",notes = "维修状态")
    @RequestMapping(value = "/findAllssbm", method = RequestMethod.GET)
    public  List<DictItem> findAllssbm(){
        return dictItemService.findAll(13L);
    }



}









